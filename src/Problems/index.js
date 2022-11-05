let result = document.getElementById("result");
let dropdown = document.getElementById("dropdown");
let editor = ace.edit(document.getElementById("code_area"), {
    theme: "ace/theme/cobalt",
    mode: "ace/mode/java"
});
let input_box = document.getElementById("input_box");

let colors = {
    cobalt: "#052440",
    dracula: "#303440",
    dreamweaver: "#ffffff",
    one_dark: "#282C34",
    monokai: "#272822",
    github: "#ffffff",
};

function change_theme() {
    let color = colors[dropdown.value];
    let char_color;

    if(color === "#ffffff") char_color = "#000000";
    else char_color = "#ffffff";

    input_box.style.color = char_color;
    result.style.color = char_color;
    input_box.style.backgroundColor = color;
    output_box.style.backgroundColor = color;

    editor.setTheme("ace/theme/" + dropdown.value);
}

function compile_code() {
    let url = "https://runjava.mcv.kr/runCode";
    let body;
    result.textContent = "실행 중 . . .";

    if (input_box.value !== "") {
        url += "WithInput"
        body = JSON.stringify({"code": editor.getValue(), "input": input_box.value})
    } else {
        body = editor.getValue();
    }

    fetch(url, {
        method: "POST",
        body: body
    }).then(async (response) => {
        result.textContent = await response.text()
    }).catch((err) => console.error(err));
}

dropdown.addEventListener("change", change_theme);
document.getElementById("submit_btn").addEventListener("click", compile_code);