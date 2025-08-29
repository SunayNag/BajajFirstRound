# Hiring SQL Submitter (Spring Boot)

## What this app does
On startup, it:
1) Calls POST `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA` with your `name`, `regNo`, and `email`.
2) Receives a `webhook` URL and an `accessToken` (JWT).
3) Picks your question: last two digits of `regNo` odd → Q1, even → Q2.
4) Loads your final SQL from `src/main/resources/sql/question1.sql` or `question2.sql` (or `app.sql.question1/2` in config).
5) Saves a local record in H2 (`submission_records`).
6) Submits `{ "finalQuery": "<YOUR_SQL>" }` to the returned `webhook` with header `Authorization: <accessToken>`.

No controllers — the flow executes via `CommandLineRunner` on application startup.

## Configure your details
Edit `src/main/resources/application.yml`:
\`\`\`yaml
app:
  applicant:
    name: "John Doe"
    regNo: "REG12347"
    email: "john@example.com"
\`\`\`

## Add your final SQL
- Q1 (odd last-two-digits): `src/main/resources/sql/question1.sql`
- Q2 (even last-two-digits): `src/main/resources/sql/question2.sql`

Replace the placeholders with your final SQL from the Google Drive doc.

## Build and run
- Build JAR:
\`\`\`
mvn clean package -DskipTests
\`\`\`
- Run:
\`\`\`
java -jar target/hiring-sql-submitter-0.0.1-SNAPSHOT.jar
\`\`\`
- Optional H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:hiringdb`
  - User: `sa`, Password: (empty)

## Protocol notes
- Submit header must be `Authorization: <accessToken>` exactly (do not prepend "Bearer " unless the token is provided that way).
- If `webhook` is blank, app falls back to `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`.

## Publish to GitHub and JAR link
1) Create a public repo and push this project.
2) Build the JAR:
\`\`\`
mvn clean package -DskipTests
\`\`\`
3) Option A: Put the JAR in a `dist/` folder, commit and push. Raw link format:
\`\`\`
https://raw.githubusercontent.com/your-username/hiring-sql-submitter/main/dist/hiring-sql-submitter-0.0.1-SNAPSHOT.jar
\`\`\`
4) Option B: Create a GitHub Release and upload the JAR; use the download URL in your submission.

## Troubleshooting
- 401/403: Ensure the `Authorization` header is exactly the returned JWT.
- 404: Ensure you submit to the returned `webhook` URL; fallback is used only if blank.
- Works in an executable JAR: SQL files are read using classpath `InputStream`, not `File`.
