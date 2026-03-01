# InterviewSim 🚀

> An open-source AI-powered technical interview simulator built with Spring Boot & Ollama.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-25-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![FOSS Hack 2026](https://img.shields.io/badge/FOSSAC-FOSS%20Hack%202026-orange.svg)]()
[![Status](https://img.shields.io/badge/Status-Active%20Development-red.svg)]()

---

## 📌 Overview

**InterviewSim** is a fully open-source platform designed to simulate real-world technical interviews — powered entirely by local, open-source AI (via [Ollama](https://ollama.com/)).

Unlike platforms that simply check if your code compiles or passes test cases, InterviewSim replicates the **full interviewer experience**:

- 🎙️ Acts as a live, conversational interviewer
- 🔍 Asks clarifying questions like a real interviewer would
- 🧠 Evaluates reasoning and thought process — not just the final answer
- 📊 Scores performance across multiple dimensions
- 📝 Provides structured, actionable feedback
- 🗺️ Suggests a personalized improvement roadmap

The goal: transform passive coding practice into **realistic, high-pressure interview preparation** — for free, for everyone.

---

## 🎯 Problem Statement

Most students preparing for technical interviews face the same gaps:

| Problem | Current Tools | InterviewSim |
|---|---|---|
| Realistic simulation | ❌ Static problems | ✅ Live AI interviewer |
| Communication evaluation | ❌ Not assessed | ✅ Scored & tracked |
| Reasoning feedback | ❌ Pass/Fail only | ✅ Step-by-step analysis |
| Improvement roadmap | ❌ Generic hints | ✅ Personalized paths |
| Cost | 💰 Paid platforms | 🆓 Fully free & open-source |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│                  Frontend                   │
│               React (Planned)               │
└──────────────────────┬──────────────────────┘
                       │ REST API
┌──────────────────────▼──────────────────────┐
│                Spring Boot Backend          │
│  ┌─────────────┐   ┌──────────────────────┐ │
│  │  REST Layer │   │   Service Layer      │ │
│  │  (Controllers)  │  (Business Logic)    │ │
│  └──────┬──────┘   └──────────┬───────────┘ │
│         │                     │             │
│  ┌──────▼─────────────────────▼───────────┐ │
│  │         Spring Data JPA                │ │
│  └──────────────────────┬─────────────────┘ │
└─────────────────────────┼───────────────────┘
                          │
          ┌───────────────▼──────────────┐
          │         MySQL                │
          └──────────────────────────────┘
                          │
          ┌───────────────▼──────────────┐
          │   AI Layer — Ollama (Local)  │
          │   Open-source LLM, no API    │
          │   keys, no data leaves your  │
          │   machine                    │
          └──────────────────────────────┘
```

**Key Design Decisions:**
- **No proprietary AI APIs** — all inference runs locally via Ollama
- **Layered architecture** — clean separation of Controller → Service → Repository
- **Privacy-first** — your code and responses never leave your machine

---

## 🛠️ Tech Stack

| Layer | Technology             |
|---|------------------------|
| Language | Java 25                |
| Framework | Spring Boot 4.x        |
| ORM | Spring Data JPA        |
| Database | MySQL                  |
| AI Runtime | Ollama (Local LLM)     |
| Build Tool | Maven                  |
| Frontend *(planned)* | React                  |
| Optional Integrations | GitHub API, Notion API |

---

## ⚙️ How to Run

### Prerequisites

- Java 25
- Maven
- MySQL
- [Ollama](https://ollama.com/) installed and running locally

### 1. Clone the Repository

```bash
git clone https://github.com/Kathir2911/interviewsim.git
cd interviewsim
```

### 2. Configure the Database

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/interviewsim
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Start Ollama

```bash
ollama pull llama3
ollama serve
```

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

The backend will start at: **http://localhost:8080**

---

## 📅 Development Roadmap

```
Week 1  ░░░░░░░░░░░░░░░░░░░░ 📋  Project setup, layered architecture, DB integration, REST APIs
Week 2  ░░░░░░░░░░░░░░░░░░░░ 📋  AI integration, interview simulation engine
Week 3  ░░░░░░░░░░░░░░░░░░░░ 📋  Scoring system, recommendation engine
Week 4  ░░░░░░░░░░░░░░░░░░░░ 📋  React UI, testing, documentation, demo prep
```

### Feature Backlog

- [x] Project structure & Spring Boot setup
- [x] Database integration with JPA
- [x] Core REST endpoints
- [ ] Ollama AI integration
- [ ] Conversational interview simulation engine
- [ ] Multi-dimensional scoring system
- [ ] Personalized feedback & improvement paths
- [ ] React frontend
- [ ] GitHub integration (profile-aware questions)
- [ ] Notion integration (save session reports)

---

## 🤝 Contributing

Contributions are welcome and appreciated!

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature-name`
3. **Commit** your changes: `git commit -m "feat: add your feature"`
4. **Push** to your branch: `git push origin feature/your-feature-name`
5. **Open** a Pull Request

Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

## 🌍 Vision

InterviewSim was built with a single belief: **quality interview preparation should be free and accessible to everyone**, regardless of geography or economic background.

By combining open-source AI with structured simulation, we aim to level the playing field for students and developers worldwide — one mock interview at a time.

---

## 📢 Status

🚧 **Active Development** — Built for **FOSS Hack 2026 (FOSSAC)**

---

## 📜 License

```
MIT License

Copyright (c) 2026 Kathir2911

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<p align="center">Made with ❤️ for the open-source community · <a href="https://github.com/Kathir2911/interviewsim">github.com/Kathir2911/interviewsim</a></p>
