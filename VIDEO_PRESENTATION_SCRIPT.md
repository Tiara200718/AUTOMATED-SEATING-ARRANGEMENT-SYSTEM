# INDIVIDUAL VIDEO PRESENTATION GUIDE & SCRIPTS
## Smart Exam Seating Arrangement System
**Duration Constraint:** Minimum **1 minute (60s)**, Maximum **2 minutes (120s)**.  
**Requirement:** **Face & Voice are MANDATORY** (Picture-in-Picture webcam overlay + screen recording).  
**Submission:** Each team member submits individually.

---

## 1. Quick Recording Setup Guide

### Recommended Free Tools:
1. **Loom (loom.com):** Easiest option. Records screen + webcam circular bubble in corner automatically. Free tier gives up to 5-minute videos.
2. **Clipchamp (Built into Windows 11):** Go to Start $\rightarrow$ Clipchamp $\rightarrow$ "Record Screen and Camera". Automatically splits your webcam and screen.
3. **OBS Studio:** Professional and free. Add "Display Capture" (Screen) + "Video Capture Device" (Webcam in corner).
4. **Windows Game Bar (`Win + G`) / Zoom / Google Meet:** Start a private meeting with yourself, turn camera on, share your screen, and click "Record".

### Video Checklist Before Recording:
- [ ] Webcam is enabled and your face is clearly visible in one corner throughout the video.
- [ ] Microphone is tested and audio is clear without background noise.
- [ ] Java application is running in the background, ready to demonstrate.
- [ ] Slides or project report is open in another tab.
- [ ] Timer is running (keep video between **1:15 and 1:45** to safely fall inside the 1-2 min range).

---

## 2. Option A: Individual All-in-One Script (90 Seconds)
*(Use this if you are presenting the complete project overview individually)*

| Timestamp | Visual on Screen | What to Say (Script) |
|---|---|---|
| **0:00 – 0:15** | Face on camera + Title Slide / GitHub page | "Hello everyone, my name is **[Your Name]**, Roll Number **[Your Roll Number]**. Today I am presenting our Java Mini Project: the **Smart Exam Seating Arrangement System**." |
| **0:15 – 0:40** | Show Problem / Architecture in PPT or Report | "Manual exam seating arrangement is a tedious and error-prone process that often leads to students of the same branch sitting adjacently, facilitating cheating. To solve this, we built a desktop application in **Java using Swing for the GUI** and **JDBC with MySQL/SQLite** for database management." |
| **0:40 – 1:15** | **Switch to the Running Java Application** | "Here is our working application. We can manage exam halls and student records. The core highlight is our **anti-cheating interleaving algorithm**. When I select an exam and click **'Generate Seating'**, the system dynamically distributes students such that adjacent seats never share the same department. Notice this interactive **visual room matrix** where CSE, ECE, and Mechanical students are alternated seamlessly." |
| **1:15 – 1:35** | Show Search feature / DB Table in App | "Students can easily search their roll number here to immediately see their hall number and seat coordinate. Everything is persisted atomically into our relational database." |
| **1:35 – 1:45** | Return to Face / GitHub repo | "All diagrams, including DFDs and Use Case, were designed in Draw.io and our source code is hosted on GitHub. Thank you!" |

---

## 3. Option B: Role-Specific Scripts (If each team member presents their assigned module)

### Member 1: Project Lead & Architecture (Focus: Problem, SRS, Stack)
* **Target Time:** ~1 minute 20 seconds
* **Visuals:** Slides 1–4, GitHub Repo, and System Architecture Diagram.
* **Script:**
  > "Hello everyone, my name is **[Your Name]**, Roll Number **[Your Roll Number]**. In our Java Mini Project, 'Smart Exam Seating Arrangement System', I served as the team lead focusing on the system architecture and requirement analysis.  
  > 
  > Manual exam seating creates massive administrative bottlenecks and increases the risk of exam malpractices. Our objective was to engineer an automated, robust desktop application. We designed this project using **Java Swing** for an intuitive GUI and **JDBC with a relational database** for transactional integrity.  
  > 
  > I led the design of the functional requirements—including admin authentication, room capacity constraints, and exportable charts. We strictly followed the Model-View-Controller design pattern and Object-Oriented principles like Encapsulation and Abstraction to keep the codebase modular.  
  > 
  > All our design specifications, DFD Level 0, Level 1, and Use Case diagrams were documented using Draw.io and version-controlled on GitHub. Thank you!"

---

### Member 2: Algorithm & Backend Logic (Focus: Interleaving Algorithm, OOP)
* **Target Time:** ~1 minute 25 seconds
* **Visuals:** Code editor highlighting `SeatingArrangementEngine.java` + Slide 7.
* **Script:**
  > "Hello everyone, I am **[Your Name]**, Roll Number **[Your Roll Number]**. My primary contribution to the Smart Exam Seating Arrangement System was developing the **core seating allocation algorithm and backend data structures**.  
  > 
  > The primary challenge was preventing cheating by ensuring that students from the same department or subject never sit next to each other. To achieve this, I implemented an **Intelligent Round-Robin Interleaving Engine** using the Java Collections Framework.  
  > 
  > In Java, we group students by their department using a `HashMap` of student queues. When populating each hall's bench matrix, our algorithm alternates between queues. If Seat 1 is allocated to a Computer Science student, Seat 2 is pulled from Electronics, and Seat 3 from Mechanical. We also validate room capacities before starting to prevent buffer overflows.  
  > 
  > This guarantees 100% anti-cheating separation and executes for 500+ students in under one second. Thank you!"

---

### Member 3: GUI & Frontend Development (Focus: Java Swing, Visual Matrix)
* **Target Time:** ~1 minute 25 seconds
* **Visuals:** Live running Java Swing application showing the Visual Matrix and Search Portal.
* **Script:**
  > "Hello faculty, I am **[Your Name]**, Roll Number **[Your Roll Number]**. I was responsible for designing and developing the **Graphical User Interface (GUI)** using Java Swing and AWT.  
  > 
  > As required by our project rubric, we built a comprehensive GUI without relying on basic command-line inputs. I designed the main dashboard with modular tabs for Student Management, Room Configuration, and Seating Generation.  
  > 
  > The centerpiece of our GUI is the **Interactive Visual Room Matrix**. Instead of a plain table, I utilized Java Swing `GridLayout` and custom `JPanel` cards to render the actual physical layout of each examination hall. Each seat displays the student's roll number and is color-coded by branch.  
  > 
  > I also implemented an instant Search feature where entering any student roll number immediately highlights their exact hall, row, column, and bench number. Thank you!"

---

### Member 4: Database Design & Integration (Focus: Schema, Normalization, JDBC)
* **Target Time:** ~1 minute 25 seconds
* **Visuals:** Database Schema (Slide 8), SQL / JDBC Code (`DatabaseManager.java`), and Report.
* **Script:**
  > "Hello everyone, my name is **[Your Name]**, Roll Number **[Your Roll Number]**. My contribution to this project was the **Relational Database Design and JDBC Connectivity**.  
  > 
  > A persistent, reliable database was a mandatory requirement. I designed a normalized relational schema up to 3NF comprising five key tables: `admin_users`, `rooms`, `students`, `exams`, and `seating_allocations`.  
  > 
  > To eliminate any possibility of accidental double-booking, I enforced a unique composite key constraint on the combination of `exam_id`, `room_id`, `bench_row`, `bench_col`, and `seat_position`.  
  > 
  > On the Java side, I implemented the Data Access Object (DAO) pattern using `PreparedStatement` to guard against SQL injection and utilized ACID transactions to ensure that if any allocation fails, the entire batch rolls back safely. Our database can be easily connected to MySQL or run locally. Thank you!"

---

## 4. Tips for High Scoring Video Presentation
1. **Pronunciation:** Speak clearly and at a steady, conversational pace.
2. **Face Position:** Position your webcam at eye level; make sure your room is well-lit.
3. **Screen Layout:** Keep your webcam in the top-right or bottom-right corner without blocking UI buttons.
4. **Show Real Action:** Clicking a button or searching a roll number live in the app adds immense credibility to your video.
