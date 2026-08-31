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
    () => {

        /*
         * 다음 UI 단계에서 구현한다.
         *
         * POST
         * /api/debates/{sessionId}/rounds/1/start
         */

        alert(
            "Round 1 UI는 다음 단계에서 연결합니다."
        );
    }
);
