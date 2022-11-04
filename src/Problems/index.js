
let btn = document.getElementById("submit_btn");
let code_area = document.getElementById("code_area");
let result = document.getElementById("result");
let dropdown = document.getElementById("dropdown");
let editor = ace.edit(code_area, {
    theme: "ace/theme/cobalt",
    mode: "ace/mode/java"
});

function change_theme(){
    editor.setTheme("ace/theme/" + dropdown.value);
}

function compile_code () {
    fetch("https://runjava.mcv.kr:53000/runCode", {
        method: "POST",
        body: editor.getValue()
    }).then(async (response) => {
        result.textContent = await response.text()
    }).catch((err) => console.error(err));
}

dropdown.addEventListener("change", change_theme);
btn.addEventListener("click", compile_code);

