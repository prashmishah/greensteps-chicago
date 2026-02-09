# Green Steps

Green Steps is a carbon-emissions monitoring platform for daily activities.

[Coding Standards and Merge Conflicts](https://github.com/depaulcdm/course-project-greensteps/wiki)

**Quick Start**
1. Backend (Spring Boot + H2 in-memory):
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
2. Frontend (Vite + React):
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

**Run Both at Once**
From the repo root:
```bash
bash scripts/dev.sh
```

**H2 Console**
When the backend is running:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:greensteps`
- User: `sa`
- Password: *(blank)*

**Notes**
- JPA automatically creates the `users` and `activities` tables on startup.
