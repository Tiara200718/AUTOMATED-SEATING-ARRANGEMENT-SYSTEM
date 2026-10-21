FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY src ./src
COPY lib ./lib
COPY exam_seating.db ./exam_seating.db

RUN mkdir -p bin && javac -cp "lib/*" -d bin $(find src -name "*.java")

CMD ["sh", "-c", "java -cp 'bin:lib/*' com.examseating.SeatingApiServer"]
