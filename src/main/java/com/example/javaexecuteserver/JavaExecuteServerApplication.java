package com.example.javaexecuteserver;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class JavaExecuteServerApplication {
	static final String workingPath = "D:\\Codes\\Java\\Javava\\src";

	public static void main(String[] args) {
		SpringApplication.run(JavaExecuteServerApplication.class, args);
	}

	@PostMapping("/runCode")
	public String simpleRunner(@RequestBody String code) {
		return runJavaCode(code,null);
	}

	@PostMapping("/runCodeWithInput")
	public String InputRunner(@RequestBody String codeAndInput) {
		try {
			var obj = new JSONObject(codeAndInput);
			return runJavaCode(obj.getString("code"), obj.getString("input"));
		} catch (JSONException e) {
			return "내부 오류 발생! : " + e.getMessage();
		}
	}

	public static String runJavaCode(String code, String input) {
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

			if (input != null){
				var writer = new BufferedWriter(process.outputWriter());
				writer.write(input);
				writer.flush();
				writer.close();
			}

			if(!process.waitFor(5,TimeUnit.SECONDS)){
				return "프로그램을 실행하는데 시간이 너무 오래 걸립니다.";
			}

			return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8) +
					new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

		} catch (Exception e) {
			e.printStackTrace();
			return "내부 오류 발생! : " + e.getMessage();
		}
	}
}
