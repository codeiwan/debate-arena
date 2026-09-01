let sessionId = null;

let debateTopic = null;

let selectedSide = null;

/* =========================================================
   DOM
========================================================= */

const screens = document.querySelectorAll(".screen");

const startButton = document.getElementById("start-button");

const topicButton = document.getElementById("topic-button");

const topicInput = document.getElementById("topic-input");

const positionAButton = document.getElementById("position-a");

const positionBButton = document.getElementById("position-b");

const round1Button = document.getElementById("round1-button");

const clarifiedTopic = document.getElementById("clarified-topic");

const positionAText = document.getElementById("position-a-text");

const positionBText = document.getElementById("position-b-text");

const userPositionText = document.getElementById("user-position");

const aiPositionText = document.getElementById("ai-position");

const loading = document.getElementById("loading");

const errorMessage = document.getElementById("error-message");

const round1AiArgument = document.getElementById("round1-ai-argument");

const round1UserInput = document.getElementById("round1-user-input");

const round1SubmitButton = document.getElementById("round1-submit-button");

const round1Result = document.getElementById("round1-result");

const round1UserPercentage = document.getElementById("round1-user-percentage");

const round1AiPercentage = document.getElementById("round1-ai-percentage");

const round1UserBar = document.getElementById("round1-user-bar");

const round1AiBar = document.getElementById("round1-ai-bar");

const round1Winner = document.getElementById("round1-winner");

const round2Button = document.getElementById("round2-button");

const round2UserInput = document.getElementById("round2-user-input");

const round2SubmitButton = document.getElementById("round2-submit-button");

const round2AiArgument = document.getElementById("round2-ai-argument");

const round2Result = document.getElementById("round2-result");

const round2UserPercentage = document.getElementById("round2-user-percentage");

const round2AiPercentage = document.getElementById("round2-ai-percentage");

const round2UserBar = document.getElementById("round2-user-bar");

const round2AiBar = document.getElementById("round2-ai-bar");

const round2Winner = document.getElementById("round2-winner");

const round3Button = document.getElementById("round3-button");

const round3AiArgument = document.getElementById("round3-ai-argument");

const round3UserInput = document.getElementById("round3-user-input");

const round3SubmitButton = document.getElementById("round3-submit-button");

const round3Result = document.getElementById("round3-result");

const round3UserPercentage = document.getElementById("round3-user-percentage");

const round3AiPercentage = document.getElementById("round3-ai-percentage");

const round3UserBar = document.getElementById("round3-user-bar");

const round3AiBar = document.getElementById("round3-ai-bar");

const round3Winner = document.getElementById("round3-winner");

const finalButton = document.getElementById("final-button");

const finalUserPercentage = document.getElementById("final-user-percentage");

const finalAiPercentage = document.getElementById("final-ai-percentage");

const finalUserBar = document.getElementById("final-user-bar");

const finalWinner = document.getElementById("final-winner");

const finalSummary = document.getElementById("final-summary");

const finalFactors = document.getElementById("final-factors");

const restartButton = document.getElementById("restart-button");

/* =========================================================
   SCREEN
========================================================= */

function showScreen(screenId) {
  screens.forEach((screen) => {
    screen.classList.remove("active");
  });

  const target = document.getElementById(screenId);

  target.classList.add("active");
}

/* =========================================================
   LOADING
========================================================= */

function showLoading() {
  loading.classList.remove("hidden");
}

function hideLoading() {
  loading.classList.add("hidden");
}

/* =========================================================
   ERROR
========================================================= */

function showError(message) {
  errorMessage.textContent = message;

  errorMessage.classList.remove("hidden");

  setTimeout(() => {
    errorMessage.classList.add("hidden");
  }, 4000);
}

/* =========================================================
   API
========================================================= */

async function apiRequest(url, options = {}) {
  const response = await fetch(url, options);

  if (!response.ok) {
    let message = "요청 처리 중 오류가 발생했습니다.";

    try {
      const error = await response.json();

      message = error.message || error.error || message;
    } catch (ignored) {}

    throw new Error(message);
  }

  return response.json();
}

/* =========================================================
   START
========================================================= */

startButton.addEventListener("click", async () => {
  try {
    showLoading();

    const session = await apiRequest("/api/debates", {
      method: "POST",
    });

    sessionId = session.sessionId;

    showScreen("topic-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

/* =========================================================
   TOPIC
========================================================= */

topicButton.addEventListener("click", async () => {
  const input = topicInput.value.trim();

  if (!input) {
    showError("토론 주제를 입력해주세요.");

    return;
  }

  try {
    showLoading();

    debateTopic = await apiRequest(`/api/debates/${sessionId}/topic`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        topic: input,
      }),
    });

    clarifiedTopic.textContent = debateTopic.topic;

    positionAText.textContent = debateTopic.positionA;

    positionBText.textContent = debateTopic.positionB;

    showScreen("position-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

/* =========================================================
   POSITION
========================================================= */

async function selectPosition(side) {
  try {
    showLoading();

    const session = await apiRequest(`/api/debates/${sessionId}/position`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        side: side,
      }),
    });

    selectedSide = side;

    userPositionText.textContent = session.userPosition;

    aiPositionText.textContent = session.aiPosition;

    showScreen("ready-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
}

positionAButton.addEventListener("click", () => selectPosition("A"));

positionBButton.addEventListener("click", () => selectPosition("B"));

/* =========================================================
   ROUND 1
========================================================= */

round1Button.addEventListener("click", async () => {
  try {
    showLoading();

    const round = await apiRequest(`/api/debates/${sessionId}/rounds/1/start`, {
      method: "POST",
    });

    round1AiArgument.textContent = round.aiArgument;

    round1UserInput.value = "";

    round1Result.classList.add("hidden");

    showScreen("round1-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

round1SubmitButton.addEventListener("click", async () => {
  const argument = round1UserInput.value.trim();

  if (!argument) {
    showError("Round 1 주장을 입력해주세요.");

    return;
  }

  try {
    showLoading();

    await apiRequest(`/api/debates/${sessionId}/rounds/1/argument`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        argument: argument,
      }),
    });

    const evaluatedRound = await apiRequest(
      `/api/debates/${sessionId}/rounds/1/evaluate`,
      {
        method: "POST",
      },
    );

    displayRound1Result(evaluatedRound.roundScore);

    round1UserInput.disabled = true;
    round1SubmitButton.disabled = true;
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

function displayRound1Result(score) {
  const userValue = score.userPercentage;

  const aiValue = score.aiPercentage;

  round1UserPercentage.textContent = `${userValue}%`;

  round1AiPercentage.textContent = `${aiValue}%`;

  round1Result.classList.remove("hidden");

  requestAnimationFrame(() => {
    round1UserBar.style.width = `${userValue}%`;

    round1AiBar.style.width = `${aiValue}%`;
  });

  if (score.winner === "USER") {
    round1Winner.textContent = "ROUND WINNER — YOU";
  } else if (score.winner === "AI") {
    round1Winner.textContent = "ROUND WINNER — AI";
  } else {
    round1Winner.textContent = "ROUND RESULT — DRAW";
  }
}

round2Button.addEventListener("click", () => {
  round2UserInput.value = "";

  round2UserInput.disabled = false;
  round2SubmitButton.disabled = false;

  round2AiArgument.textContent = "당신의 반박을 기다리고 있습니다.";

  round2Result.classList.add("hidden");

  showScreen("round2-screen");
});

round2SubmitButton.addEventListener("click", async () => {
  const argument = round2UserInput.value.trim();

  if (!argument) {
    showError("Round 2 반박을 입력해주세요.");

    return;
  }

  try {
    showLoading();

    await apiRequest(`/api/debates/${sessionId}/rounds/2/argument`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        argument: argument,
      }),
    });

    const aiRound = await apiRequest(
      `/api/debates/${sessionId}/rounds/2/respond`,
      {
        method: "POST",
      },
    );

    round2AiArgument.textContent = aiRound.aiArgument;

    const evaluatedRound = await apiRequest(
      `/api/debates/${sessionId}/rounds/2/evaluate`,
      {
        method: "POST",
      },
    );

    displayRound2Result(evaluatedRound.roundScore);

    round2UserInput.disabled = true;
    round2SubmitButton.disabled = true;
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

function displayRound2Result(score) {
  const userValue = score.userPercentage;

  const aiValue = score.aiPercentage;

  round2UserPercentage.textContent = `${userValue}%`;

  round2AiPercentage.textContent = `${aiValue}%`;

  round2Result.classList.remove("hidden");

  requestAnimationFrame(() => {
    round2UserBar.style.width = `${userValue}%`;

    round2AiBar.style.width = `${aiValue}%`;
  });

  if (score.winner === "USER") {
    round2Winner.textContent = "ROUND WINNER — YOU";
  } else if (score.winner === "AI") {
    round2Winner.textContent = "ROUND WINNER — AI";
  } else {
    round2Winner.textContent = "ROUND RESULT — DRAW";
  }
}

round3Button.addEventListener("click", async () => {
  try {
    showLoading();

    const round = await apiRequest(`/api/debates/${sessionId}/rounds/3/start`, {
      method: "POST",
    });

    round3AiArgument.textContent = round.aiArgument;

    round3UserInput.value = "";
    round3UserInput.disabled = false;
    round3SubmitButton.disabled = false;

    round3Result.classList.add("hidden");

    showScreen("round3-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

round3SubmitButton.addEventListener("click", async () => {
  const argument = round3UserInput.value.trim();

  if (!argument) {
    showError("최종 변론을 입력해주세요.");

    return;
  }

  try {
    showLoading();

    await apiRequest(`/api/debates/${sessionId}/rounds/3/argument`, {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        argument: argument,
      }),
    });

    const evaluatedRound = await apiRequest(
      `/api/debates/${sessionId}/rounds/3/evaluate`,
      {
        method: "POST",
      },
    );

    displayRound3Result(evaluatedRound.roundScore);

    round3UserInput.disabled = true;
    round3SubmitButton.disabled = true;
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

function displayRound3Result(score) {
  const userValue = score.userPercentage;

  const aiValue = score.aiPercentage;

  round3UserPercentage.textContent = `${userValue}%`;

  round3AiPercentage.textContent = `${aiValue}%`;

  round3Result.classList.remove("hidden");

  requestAnimationFrame(() => {
    round3UserBar.style.width = `${userValue}%`;

    round3AiBar.style.width = `${aiValue}%`;
  });

  if (score.winner === "USER") {
    round3Winner.textContent = "ROUND WINNER — YOU";
  } else if (score.winner === "AI") {
    round3Winner.textContent = "ROUND WINNER — AI";
  } else {
    round3Winner.textContent = "ROUND RESULT — DRAW";
  }
}

finalButton.addEventListener("click", async () => {
  try {
    showLoading();

    const verdict = await apiRequest(`/api/debates/${sessionId}/finalize`, {
      method: "POST",
    });

    displayFinalVerdict(verdict);

    showScreen("final-screen");
  } catch (error) {
    showError(error.message);
  } finally {
    hideLoading();
  }
});

function displayFinalVerdict(verdict) {
  finalUserPercentage.textContent = `${verdict.userPercentage}%`;

  finalAiPercentage.textContent = `${verdict.aiPercentage}%`;

  requestAnimationFrame(() => {
    finalUserBar.style.width = `${verdict.userPercentage}%`;
  });

  if (verdict.winner === "USER") {
    finalWinner.textContent = "🏆 YOU WIN";
  } else if (verdict.winner === "AI") {
    finalWinner.textContent = "🏆 AI WINS";
  } else {
    finalWinner.textContent = "DRAW";
  }

  finalSummary.textContent = verdict.summary;

  finalFactors.innerHTML = "";

  verdict.decisiveFactors.forEach((factor) => {
    const item = document.createElement("li");

    item.textContent = factor;

    finalFactors.appendChild(item);
  });
}

restartButton.addEventListener("click", () => {
  window.location.reload();
});
