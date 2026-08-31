let sessionId = null;

let debateTopic = null;

let selectedSide = null;


/* =========================================================
   DOM
========================================================= */

const screens = document.querySelectorAll(".screen");

const startButton =
    document.getElementById("start-button");

const topicButton =
    document.getElementById("topic-button");

const topicInput =
    document.getElementById("topic-input");

const positionAButton =
    document.getElementById("position-a");

const positionBButton =
    document.getElementById("position-b");

const round1Button =
    document.getElementById("round1-button");

const clarifiedTopic =
    document.getElementById("clarified-topic");

const positionAText =
    document.getElementById("position-a-text");

const positionBText =
    document.getElementById("position-b-text");

const userPositionText =
    document.getElementById("user-position");

const aiPositionText =
    document.getElementById("ai-position");

const loading =
    document.getElementById("loading");

const errorMessage =
    document.getElementById("error-message");

const round1AiArgument =
    document.getElementById("round1-ai-argument");

const round1UserInput =
    document.getElementById("round1-user-input");

const round1SubmitButton =
    document.getElementById("round1-submit-button");

const round1Result =
    document.getElementById("round1-result");

const round1UserPercentage =
    document.getElementById("round1-user-percentage");

const round1AiPercentage =
    document.getElementById("round1-ai-percentage");

const round1UserBar =
    document.getElementById("round1-user-bar");

const round1AiBar =
    document.getElementById("round1-ai-bar");

const round1Winner =
    document.getElementById("round1-winner");

const round2Button =
    document.getElementById("round2-button");

/* =========================================================
   SCREEN
========================================================= */

function showScreen(screenId) {

    screens.forEach(screen => {
        screen.classList.remove("active");
    });

    const target =
        document.getElementById(screenId);

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

        let message =
            "요청 처리 중 오류가 발생했습니다.";

        try {

            const error =
                await response.json();

            message =
                error.message
                || error.error
                || message;

        } catch (ignored) {
        }

        throw new Error(message);
    }

    return response.json();
}


/* =========================================================
   START
========================================================= */

startButton.addEventListener(
    "click",
    async () => {

        try {

            showLoading();

            const session =
                await apiRequest(
                    "/api/debates",
                    {
                        method: "POST"
                    }
                );

            sessionId =
                session.sessionId;

            showScreen("topic-screen");

        } catch (error) {

            showError(error.message);

        } finally {

            hideLoading();
        }
    }
);


/* =========================================================
   TOPIC
========================================================= */

topicButton.addEventListener(
    "click",
    async () => {

        const input =
            topicInput.value.trim();

        if (!input) {

            showError(
                "토론 주제를 입력해주세요."
            );

            return;
        }

        try {

            showLoading();

            debateTopic =
                await apiRequest(
                    `/api/debates/${sessionId}/topic`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            topic: input
                        })
                    }
                );

            clarifiedTopic.textContent =
                debateTopic.topic;

            positionAText.textContent =
                debateTopic.positionA;

            positionBText.textContent =
                debateTopic.positionB;

            showScreen(
                "position-screen"
            );

        } catch (error) {

            showError(error.message);

        } finally {

            hideLoading();
        }
    }
);


/* =========================================================
   POSITION
========================================================= */

async function selectPosition(side) {

    try {

        showLoading();

        const session =
            await apiRequest(
                `/api/debates/${sessionId}/position`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        side: side
                    })
                }
            );

        selectedSide = side;

        userPositionText.textContent =
            session.userPosition;

        aiPositionText.textContent =
            session.aiPosition;

        showScreen(
            "ready-screen"
        );

    } catch (error) {

        showError(error.message);

    } finally {

        hideLoading();
    }
}


positionAButton.addEventListener(
    "click",
    () => selectPosition("A")
);


positionBButton.addEventListener(
    "click",
    () => selectPosition("B")
);


/* =========================================================
   ROUND 1
========================================================= */

round1Button.addEventListener(
    "click",
    async () => {

        try {

            showLoading();

            const round =
                await apiRequest(
                    `/api/debates/${sessionId}/rounds/1/start`,
                    {
                        method: "POST"
                    }
                );

            round1AiArgument.textContent =
                round.aiArgument;

            round1UserInput.value = "";

            round1Result.classList.add("hidden");

            showScreen("round1-screen");

        } catch (error) {

            showError(error.message);

        } finally {

            hideLoading();
        }
    }
);

round1SubmitButton.addEventListener(
    "click",
    async () => {

        const argument =
            round1UserInput.value.trim();

        if (!argument) {

            showError(
                "Round 1 주장을 입력해주세요."
            );

            return;
        }

        try {

            showLoading();

            await apiRequest(
                `/api/debates/${sessionId}/rounds/1/argument`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        argument: argument
                    })
                }
            );


            const evaluatedRound =
                await apiRequest(
                    `/api/debates/${sessionId}/rounds/1/evaluate`,
                    {
                        method: "POST"
                    }
                );


            displayRound1Result(
                evaluatedRound.roundScore
            );

            round1UserInput.disabled = true;
            round1SubmitButton.disabled = true;

        } catch (error) {

            showError(error.message);

        } finally {

            hideLoading();
        }
    }
);

function displayRound1Result(score) {

    const userValue =
        score.userPercentage;

    const aiValue =
        score.aiPercentage;


    round1UserPercentage.textContent =
        `${userValue}%`;

    round1AiPercentage.textContent =
        `${aiValue}%`;


    round1Result.classList.remove(
        "hidden"
    );


    requestAnimationFrame(() => {

        round1UserBar.style.width =
            `${userValue}%`;

        round1AiBar.style.width =
            `${aiValue}%`;
    });


    if (score.winner === "USER") {

        round1Winner.textContent =
            "ROUND WINNER — YOU";

    } else if (score.winner === "AI") {

        round1Winner.textContent =
            "ROUND WINNER — AI";

    } else {

        round1Winner.textContent =
            "ROUND RESULT — DRAW";
    }
}

round2Button.addEventListener(
    "click",
    () => {

        alert(
            "Round 2 UI는 다음 단계에서 연결합니다."
        );
    }
);
