# MCQ System  
A complete MCQ (Multiple Choice Questions) platform consisting of a **Client Application** and a **Server Backend**.

---

## 📌 Overview  
This project is a two-module MCQ system:

### **1. MCQ Client (Frontend)**
A Java-based UI application that allows users to:
- Fetch MCQ questions from the server  
- Attempt quizzes  
- Submit answers  
- View scores  

### **2. MCQ Server (Backend)**
A Java (Maven/Spring-based) backend that:
- Provides MCQ questions through REST APIs  
- Accepts submitted answers  
- Calculates and returns scores  

Both modules work together to form a complete MCQ evaluation system.

---

## 📁 Project Structure  
mcq_system/
│
├── mcq_client/ → Client-side Java app
└── mcq_server/ → Server-side Java backend



---

## 🚀 Features

### **Client Features**
- Fetches quiz/questions from server  
- Displays MCQs in clean UI  
- Allows answer selection  
- Submits responses  
- Shows score returned from backend  

### **Server Features**
- REST APIs for MCQs  
- Validates submitted answers  
- Computes score  
- Sends JSON responses  
- Easy to extend with more question sets  

---

## 🛠️ Technologies Used  
- **Java (JDK 8+)**  
- **Maven**  
- **Spring Boot (server)**  
- **HTTP/JSON communication**  

---

## 🧩 How It Works

### **1. Client requests MCQs**
GET /api/questions

shell
Copy code

### **2. Server returns question list (JSON)**  
Client displays them to the user.

### **3. Client submits answers**
POST /api/submit

makefile
Copy code

### **4. Server calculates score**  
Returns:
```json
{
  "score": 8,
  "total": 10
}
🛠️ Setup Instructions
Clone both repositories
bash
Copy code
git clone https://github.com/MadhavK3/mcq_client.git
git clone https://github.com/MadhavK3/mcq_server.git
▶️ Running the Server (Backend)
bash
Copy code
cd mcq_server
mvn clean install
mvn spring-boot:run
Default port: 8080

▶️ Running the Client (Frontend)

cd mcq_client
mvn clean install
Then run the main Java file from your IDE or terminal.

Ensure the server is running before starting the client.

📌 Future Enhancements
Add login/authentication

Timer for each quiz

Category-wise MCQs

Leaderboard & analytics

Persistent DB (MySQL/PostgreSQL)

Admin panel for adding MCQs

👨‍💻 Authors

Name	PRN
Om	    001
Atharva	009
Madhav	023



📚 References
Java Documentation

Spring Boot Docs

TutorialsPoint

GeeksForGeeks

Javatpoint