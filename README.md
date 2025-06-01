# 🎬 Кінопроєкт — Backend & Frontend

## 🌐 Деплой
### 🖥️ Frontend
- 🌍 [https://mellow-dolphin-38542e.netlify.app](https://mellow-dolphin-38542e.netlify.app)
- 🔧 Built with: Vue.js

### ⚙️ Backend
- 🌍 [https://film-no9d.onrender.com](https://film-no9d.onrender.com)
- 🔧 Built with: Java Spring , Postgres
---

## ✅ Виконані завдання

### 📦 Архітектура та логіка

- 🧩 **Розроблено REST-контролери** для обробки HTTP-запитів користувача (автентифікація, реєстрація, отримання інформації тощо)
- ⚙️ **Імплементовано сервісний рівень** із розподіленням бізнес-логіки окремо від контролерів
- 🧱 **Проєктування сутностей** із чітко визначеними зв’язками між об’єктами

### 🗄️ Робота з базою даних

- 🐘 **Підключено реляційну базу даних PostgreSQL**
- 🔗 **Створено основні сутності**:
  - `User` – головна сутність користувача
  - Один-до-одного зв'язки:
    - `Actor` – актор, пов'язаний із користувачем
    - `Director` – режисер
    - `CrewMember` – член знімальної групи
    - `Visitor` – відвідувач сайту
- 📥 **Репозиторії реалізовано через Spring Data JPA**

### 🔐 Аутентифікація та авторизація

- 🔑 **Підтримка JWT (JSON Web Token)** — для всіх запитів, автентифікація реалізована через токени
- 🌍 **OAuth2 з Google** — реалізовано авторизацію через обліковий запис Google
- 🛡️ **Захищені ендпоінти** з використанням Spring Security та фільтрів безпеки

### 🧾 Реєстрація та логін

- 🧑‍💻 **Форма реєстрації/логіну через frontend (Vue.js)** — взаємодія через REST API
- ✅ Реєстрація нового користувача, автентифікація з поверненням JWT токена
- 🔐 Вхід через Google акаунт (OAuth2 flow)

### 📊 Логи та моніторинг

- 📦 **Інтегровано логування через стек ELK (Elasticsearch + Logstash + Kibana)** — протестовано локально
- 🔍 **Формат логів у JSON**, що дозволяє зручно аналізувати запити/відповіді

### 📁 Репозиторії

- 📂 **Backend (Spring Boot)** — окремий репозиторій із серверною частиною
- 🖼️ **Frontend (Vue.js)** — окремий репозиторій із клієнтською частиною

### ☁️ Деплой

- 🚀 **Backend розгорнутий на Render**
- 🌍 **Frontend розгорнутий на Netlify**
- 👥 Обидві частини проєкту доступні для публічного перегляду та взаємодії

---

## 📌 Технології

| Сторона       | Стек                                                                 |
|---------------|----------------------------------------------------------------------|
| Backend       | Java, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL     |
| Frontend      | Vue.js, Axios                                                        |
| Auth          | JWT, Google OAuth2                                                   |
| Logs & DevOps | ELK Stack (локально), Render, Netlify                               |

---

## 📎 Скріншоти (опційно)

 скріншоти форм логіну, відповіді API : 
![image](https://github.com/user-attachments/assets/6e893717-1c5e-4355-8013-855dcdc8cb9e)
![image](https://github.com/user-attachments/assets/401782ef-6b25-44d0-ad10-6eba821240ea)
![image](https://github.com/user-attachments/assets/f0acbec0-104e-4644-91c6-f86933fe65df)
![image](https://github.com/user-attachments/assets/b08b1dbb-a792-4b4f-a066-b848477133d7)
![image](https://github.com/user-attachments/assets/7bb59d0c-1da5-479a-88d7-a94d9bb61407)

---

## 📬 Зворотний зв’язок

Буду радий будь-яким відгукам, пропозиціям чи pull-request'ам 🚀

