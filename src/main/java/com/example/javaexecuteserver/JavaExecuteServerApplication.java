package com.example.javaexecuteserver;

import java.io.File;
import java.io.FileWriter;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class JavaExecuteServerApplication {
	static final String workingPath = "D:\\Codes\\Java\\Javava\\src";

	public static void main(String[] args) {
		SpringApplication.run(JavaExecuteServerApplication.class, args);
	}

	@PostMapping("/runCode")
	public String runner(@RequestBody String code) {
		return runJavaCode(code);
	}

	public static String runJavaCode(String code) {
		ProcessBuilder builder = new ProcessBuilder();
		builder.directory(new File(workingPath));

		try {
			FileWriter javaFile = new FileWriter(workingPath + "\\Main.java");
			javaFile.write(code);
			javaFile.close();

			builder.command(
					"cmd",
					"/c",
					"javac Main.java"
			);
			Process compile = builder.start();
			compile.waitFor();
			if(compile.getErrorStream().available() > 0){
				return new String(compile.getErrorStream().readAllBytes(),StandardCharsets.UTF_8);
			}

			builder.command(
					"cmd",
					"/c",
					"java Main"
			);
			Process process = builder.start();

			return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8) +
					new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

		} catch (Exception e) {
			e.printStackTrace();
			return "내부 오류 발생!";
		}
	}
}
