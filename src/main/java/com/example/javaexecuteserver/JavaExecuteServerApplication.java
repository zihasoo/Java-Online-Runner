package com.example.javaexecuteserver;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class JavaExecuteServerApplication {
    static final String workingPath = "D:\\Codes\\Java\\Java Execute Server\\src\\CodePool";

    static String compileCCommand = "gcc Main.c -O2 -o Main.exe -static";
    static String compileCppCommand = "g++ Main.cpp -O2 -o Main.exe -std=c++17 -static";
    static String runCCppCommand = "Main.exe";
    static String compileCsCommand = "dotnet new console ~~~";
    static String runCsCommand = "Main.exe ~~~";
    static String compileJavaCommand = "javac Main.java";
    static String runJavaCommand = "java Main";
    static String compilePythonCommand = "py main.py";
    static String runPythonCommand = "~~~~";

    public static void main(String[] args) {
        SpringApplication.run(JavaExecuteServerApplication.class, args);
    }

    @PostMapping("/runCode")
    public String InputRunner(@RequestHeader Map<String, String> header, @RequestBody String codeAndInput) {
        try {
            String lang = header.get("lang");
            String hasInput = header.get("hasinput");

            if (hasInput.equals("true")) { //여기부터 다른 스레드에서 하면 좋을듯
                var obj = new JSONObject(codeAndInput);
                return runCode(obj.getString("code"), obj.getString("input"), lang);
            } else {
                return runCode(codeAndInput, null, lang);
            }

        } catch (Exception e) {
            return "내부 오류 발생! : " + e.getMessage();
        }
    }

    public static String runCode(String code, String input, String lang) throws Exception {
        var codeFile = new FileWriter(workingPath + "\\Main." + lang); //이걸 비동기로 하면 좋을듯
        codeFile.write(code);
        codeFile.close();

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(new File(workingPath));

        String compileCommand;
        String runCommand;

        switch (lang) {
            case "java":
                compileCommand = compileJavaCommand;
                runCommand = runJavaCommand;
                break;
            case "c":
                compileCommand = compileCCommand;
                runCommand = runCCppCommand;
                break;
            case "cpp":
                compileCommand = compileCppCommand;
                runCommand = runCCppCommand;
                break;
            case "cs":
            case "python":
                return "구현 예정";
            default:
                return "잘못된 언어 요청";
        }

        builder.command("cmd","/C", compileCommand);
        Process compile = builder.start();
        compile.waitFor();
        if (compile.getErrorStream().available() > 0) {
            return new String(compile.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        }

        builder.command("cmd","/C", runCommand);
        Process process = builder.start();

        if (input != null) {
            var writer = new BufferedWriter(process.outputWriter());
            writer.write(input);
            writer.flush();
            writer.close();
        }

        if (!process.waitFor(5, TimeUnit.SECONDS)) {
            return "프로그램을 실행하는데 시간이 너무 오래 걸립니다.";
        }

        return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8) +
                new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

    }
}
