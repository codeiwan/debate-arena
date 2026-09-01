# Debate Arena

Spring Boot와 Spring AI를 활용한 **3라운드 AI 토론 게임**입니다.

사용자가 자유롭게 입력한 주제를 AI가 토론 가능한 논제로 정제하고, 사용자가 찬반 입장 중 하나를 선택하면 AI가 반대편 토론자가 되어 3라운드 토론을 진행합니다. 각 라운드가 끝날 때마다 별도의 AI 심판이 양측의 발언을 평가하고, 최종적으로 전체 토론 결과와 승패를 판정합니다.

## 주요 기능

- 자유 입력 주제를 토론 가능한 논제로 자동 정제
- AI가 생성한 두 입장 중 사용자 진영 선택
- 사용자와 AI의 1:1, 3라운드 토론
- 라운드별 대화 메모리를 활용한 맥락 유지
- AI 심판의 구조화된 평가 결과 생성
- 논리성, 관련성, 구체성, 설득력 기반 점수 계산
- 라운드별 승자 및 점수 비율 시각화
- 3개 라운드를 종합한 최종 승자 판정
- 최종 판정 요약과 승패를 가른 핵심 요인 제공
- 별도 프론트엔드 프레임워크 없이 HTML/CSS/JavaScript 기반 UI 제공

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring AI 2.0.1
- OpenAI GPT-4o-mini
- Gradle

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

## Debate Flow

```text
START
  │
  ▼
Create Debate Session
  │
  ▼
사용자 주제 입력
  │
  ▼
AI Topic Clarifier
  │
  ├─ 토론 논제 생성
  ├─ Position A 생성
  └─ Position B 생성
  │
  ▼
사용자 입장 선택
  │
  ▼
┌───────────────────────────┐
│ Round 1 - Opening Argument│
│ AI 주장 → 사용자 주장     │
│          ↓                │
│       AI Judge            │
└───────────────────────────┘
  │
  ▼
┌───────────────────────────┐
│ Round 2 - Rebuttal        │
│ 사용자 반박 → AI 반박     │
│          ↓                │
│       AI Judge            │
└───────────────────────────┘
  │
  ▼
┌───────────────────────────┐
│ Round 3 - Final Statement │
│ AI 최종 변론 → 사용자 변론│
│          ↓                │
│       AI Judge            │
└───────────────────────────┘
  │
  ▼
Final Score
  │
  ▼
Final AI Judge
  │
  ▼
Winner + Summary + Decisive Factors
```

## Architecture

```text
Browser
  │
  │ Fetch API
  ▼
DebateController
  │
  ▼
DebateService
  │
  ├──────────────────────┐
  ▼                      ▼
DebateAiService       JudgeService
  │                      │
  │                      ├─ Round Judge
  │                      └─ Final Judge
  │
  ▼
ChatClient + ChatMemory
  │
  ▼
OpenAI GPT-4o-mini

DebateService
  │
  ├─ Session State Management
  └─ ScoreService
         └─ Round / Final Score Calculation
```

### 역할 분리

- `DebateController`: 토론 진행에 필요한 REST API 제공
- `DebateService`: 세션 상태와 3라운드 진행 순서 관리
- `DebateAiService`: 토론 주제 정제 및 AI 토론자의 발언 생성
- `JudgeService`: 각 라운드와 최종 결과에 대한 AI 심판 역할
- `ScoreService`: 심판 점수를 바탕으로 승자와 점수 비율 계산
- `ChatMemory`: 같은 토론 세션 안에서 이전 발언 맥락 유지

## AI Design

### 1. Topic Clarifier

사용자가 자유롭게 입력한 주제를 바로 토론에 사용하지 않고 AI가 다음 구조로 정제합니다.

```json
{
  "topic": "정제된 토론 논제",
  "positionA": "첫 번째 입장",
  "positionB": "두 번째 입장"
}
```

두 입장이 직접 대립하면서도 한쪽이 지나치게 유리하지 않도록 프롬프트를 구성했습니다.

### 2. Session-based Chat Memory

각 토론 세션의 `sessionId`를 Spring AI의 `ChatMemory.CONVERSATION_ID`로 사용합니다.

```text
Debate Session A
├─ AI Round 1
├─ User Round 1
├─ User Round 2
├─ AI Round 2
└─ ...

Debate Session B
└─ 별도의 독립된 대화 맥락
```

이를 통해 AI는 Round 2와 Round 3에서 앞선 라운드의 실제 발언을 참고할 수 있습니다.

현재 메모리는 `MessageWindowChatMemory`를 사용하며 최대 20개의 메시지를 유지합니다.

### 3. Prompt Separation

AI의 역할별 프롬프트를 Java 코드에 직접 작성하지 않고 별도 파일로 분리했습니다.

```text
src/main/resources/prompts/
├── topic-clarifier.st
├── debater.st
├── rebuttal.st
├── final-statement.st
├── judge.st
└── final-judge.st
```

각 프롬프트는 다음 역할을 담당합니다.

| Prompt | Role |
| --- | --- |
| `topic-clarifier.st` | 자유 입력을 균형 잡힌 토론 논제로 변환 |
| `debater.st` | Round 1 AI 첫 주장 생성 |
| `rebuttal.st` | Round 2에서 실제 상대 발언을 기반으로 반박 |
| `final-statement.st` | 전체 토론을 종합한 Round 3 최종 변론 |
| `judge.st` | 각 라운드 양측 발언 평가 |
| `final-judge.st` | 세 라운드 결과를 종합한 최종 판정 설명 |

### 4. Structured Output

토론 주제와 AI 심판 결과는 문자열을 임의로 파싱하지 않고 Spring AI의 Structured Output을 사용합니다.

```text
AI Response
   ↓
JSON Schema Validation
   ↓
Java Record
```

주요 구조화 객체:

- `DebateTopic`
- `JudgeResult`
- `FinalJudgeResult`

## Scoring System

AI 심판은 사용자와 AI를 각각 네 항목으로 평가합니다.

| Metric | Description |
| --- | --- |
| `logic` | 주장과 근거의 논리적 연결 및 내부 일관성 |
| `relevance` | 논제와 현재 라운드 목적에 대한 관련성 |
| `specificity` | 주장과 근거의 구체성 |
| `persuasiveness` | 전체적인 설득력 |

각 항목은 0~100점입니다.

각 진영의 라운드 점수는 네 항목의 평균으로 계산합니다.

```text
Round Score
= (logic + relevance + specificity + persuasiveness) / 4
```

양측 점수를 이용해 상대적인 비율을 계산합니다.

```text
User Percentage = User Score / (User Score + AI Score) × 100
AI Percentage   = AI Score   / (User Score + AI Score) × 100
```

최종 점수는 세 라운드의 평균 점수를 이용합니다.

AI가 직접 최종 점수를 결정하는 구조가 아니라, **심판의 항목별 평가 → 애플리케이션 점수 계산 → AI의 최종 설명**으로 역할을 분리했습니다.

## Session State

토론 진행 순서는 `DebateSession.status`로 관리합니다.

```text
CREATED
  ↓
TOPIC_READY
  ↓
POSITION_SELECTED
  ↓
ROUND_1_AI_DONE
  ↓
ROUND_1_ARGUMENTS_COMPLETE
  ↓
ROUND_1_EVALUATED
  ↓
ROUND_2_USER_DONE
  ↓
ROUND_2_ARGUMENTS_COMPLETE
  ↓
ROUND_2_EVALUATED
  ↓
ROUND_3_AI_DONE
  ↓
ROUND_3_ARGUMENTS_COMPLETE
  ↓
ROUND_3_EVALUATED
  ↓
COMPLETED
```

잘못된 순서로 API가 호출되면 `IllegalStateException`을 발생시켜 토론 상태가 비정상적으로 진행되는 것을 방지합니다.

## API

Base URL:

```text
http://localhost:8080
```

### Create Debate Session

```http
POST /api/debates
```

새로운 `sessionId`를 생성합니다.

### Get Debate Session

```http
GET /api/debates/{sessionId}
```

현재 토론 세션의 상태, 입장, 라운드 및 최종 결과를 조회합니다.

### Clarify Topic

```http
POST /api/debates/{sessionId}/topic
Content-Type: application/json
```

```json
{
  "topic": "AI 코딩 도구에 대해서 토론하고 싶어"
}
```

### Select Position

```http
POST /api/debates/{sessionId}/position
Content-Type: application/json
```

```json
{
  "side": "A"
}
```

`A` 또는 `B`를 선택할 수 있으며 AI는 자동으로 반대 입장을 맡습니다.

### Round 1

```http
POST /api/debates/{sessionId}/rounds/1/start
POST /api/debates/{sessionId}/rounds/1/argument
POST /api/debates/{sessionId}/rounds/1/evaluate
```

사용자 주장 제출:

```json
{
  "argument": "사용자의 Round 1 주장"
}
```

### Round 2

```http
POST /api/debates/{sessionId}/rounds/2/argument
POST /api/debates/{sessionId}/rounds/2/respond
POST /api/debates/{sessionId}/rounds/2/evaluate
```

### Round 3

```http
POST /api/debates/{sessionId}/rounds/3/start
POST /api/debates/{sessionId}/rounds/3/argument
POST /api/debates/{sessionId}/rounds/3/evaluate
```

### Final Verdict

```http
POST /api/debates/{sessionId}/finalize
```

예시 구조:

```json
{
  "userPercentage": 53.2,
  "aiPercentage": 46.8,
  "winner": "USER",
  "summary": "세 라운드를 종합한 최종 판정 설명",
  "decisiveFactors": [
    "핵심 승부 요인 1",
    "핵심 승부 요인 2"
  ]
}
```

## Project Structure

```text
debate-arena/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradle/
│
└── src/
    └── main/
        ├── java/com/example/debatearena/
        │   ├── DebateArenaApplication.java
        │   │
        │   ├── config/
        │   │   └── AiConfig.java
        │   │
        │   ├── controller/
        │   │   ├── AiTestController.java
        │   │   └── DebateController.java
        │   │
        │   ├── domain/
        │   │   ├── DebateSession.java
        │   │   ├── DebateRound.java
        │   │   ├── DebateTopic.java
        │   │   ├── JudgeResult.java
        │   │   ├── RoundScore.java
        │   │   ├── FinalJudgeResult.java
        │   │   └── FinalVerdict.java
        │   │
        │   ├── dto/
        │   │   ├── TopicRequest.java
        │   │   ├── PositionRequest.java
        │   │   └── ArgumentRequest.java
        │   │
        │   └── service/
        │       ├── DebateService.java
        │       ├── DebateAiService.java
        │       ├── JudgeService.java
        │       └── ScoreService.java
        │
        └── resources/
            ├── application.yml
            ├── prompts/
            │   ├── topic-clarifier.st
            │   ├── debater.st
            │   ├── rebuttal.st
            │   ├── final-statement.st
            │   ├── judge.st
            │   └── final-judge.st
            │
            └── static/
                ├── index.html
                ├── app.js
                └── style.css
```

## Configuration

OpenAI API Key는 환경변수로 주입합니다.

```bash
export OPENAI_API_KEY="your-api-key"
```

`application.yml`:

```yaml
spring:
  application:
    name: debate-arena

  ai:
    openai:
      api-key: ${OPENAI_API_KEY:}
      chat:
        model: gpt-4o-mini
```

API Key를 소스 코드나 Git 저장소에 직접 커밋하지 않습니다.

## Run

macOS / Linux:

```bash
./gradlew bootRun
```

Windows:

```bat
gradlew.bat bootRun
```

실행 후 브라우저에서 접속합니다.

```text
http://localhost:8080
```

## Demo Scenario

1. `START BATTLE`을 클릭해 새로운 토론 세션을 생성합니다.
2. 토론하고 싶은 주제를 자유롭게 입력합니다.
3. AI가 정제한 논제와 두 입장 중 하나를 선택합니다.
4. Round 1에서 AI의 첫 주장을 확인하고 자신의 주장을 제출합니다.
5. AI 심판의 Round 1 점수와 승자를 확인합니다.
6. Round 2에서 AI의 이전 주장을 반박하면 AI도 지금까지의 대화 맥락을 바탕으로 반박합니다.
7. Round 3에서 양측이 최종 변론을 진행합니다.
8. 세 라운드 점수의 평균과 AI 최종 심판의 설명을 통해 최종 결과를 확인합니다.

## Design Points

### AI에게 모든 판단을 맡기지 않음

AI 심판은 항목별 평가를 담당하지만 점수 평균, 비율 계산 및 최종 승자 계산은 `ScoreService`에서 수행합니다. 이를 통해 생성형 AI의 자연어 판정과 애플리케이션의 결정적 계산 로직을 분리했습니다.

### 실제 발언 기반 반박

Round 2와 Round 3 프롬프트는 상대방이 하지 않은 말을 임의로 만들어내지 않도록 제한하고, `ChatMemory`에 저장된 실제 토론 맥락을 사용하도록 설계했습니다.

### 단계별 상태 검증

각 API는 현재 `DebateSession.status`를 검증한 뒤 다음 단계로 진행합니다. 따라서 Round 1 평가 전 Round 2를 호출하는 식의 비정상적인 상태 전이를 방지합니다.

### Structured Output 활용

토론 논제 및 심판 결과처럼 프로그램에서 후속 처리가 필요한 응답은 Structured Output과 Schema Validation을 사용하여 Java 객체로 직접 변환합니다.

## Current Limitations

현재 프로젝트는 학습 및 로컬 실습을 목적으로 한 MVP입니다.

- `DebateSession`은 `ConcurrentHashMap`에 저장되어 애플리케이션 재시작 시 사라집니다.
- `ChatMemory` 역시 인메모리 방식입니다.
- 사용자 인증 및 사용자별 접근 제어가 없습니다.
- API 호출량 제한과 비용 계측 기능이 없습니다.
- AI 호출 실패에 대한 재시도 및 세밀한 예외 응답 처리가 제한적입니다.
- 자동화된 서비스/AI 품질 테스트가 충분히 구성되어 있지 않습니다.

## Future Improvements

- Redis 또는 RDB 기반 토론 세션 영속화
- Redis 기반 ChatMemory 적용
- 사용자 인증 및 사용자별 토론 기록 제공
- SSE 기반 AI 발언 스트리밍
- Actuator/Micrometer 기반 AI 호출 계측
- API 사용량 및 비용 제한
- 프롬프트 및 심판 평가 품질 테스트
- 토론 기록 다시보기 및 공유 기능
- 주제별 리더보드 및 승률 통계
- Docker 기반 배포 환경 구성

## Summary

Debate Arena는 단순한 Chat API가 아니라 다음 요소를 하나의 흐름으로 결합한 Spring AI 프로젝트입니다.

```text
Structured Output
+ Prompt Engineering
+ Chat Memory
+ Multi-step Session State
+ AI Debater
+ AI Judge
+ Deterministic Scoring
+ Browser UI
```

사용자는 토론 주제를 입력하는 순간부터 입장 선택, 세 라운드의 논쟁, 라운드별 평가, 최종 판정까지 하나의 완결된 AI 토론 경험을 진행할 수 있습니다.
