
let result = document.getElementById("result");
let dropdown = document.getElementById("dropdown");
let editor = ace.edit(document.getElementById("code_area"), {
    theme: "ace/theme/cobalt",
    mode: "ace/mode/java"
});
let input_box = document.getElementById("input_box");

function change_theme(){
    editor.setTheme("ace/theme/" + dropdown.value);
}

function compile_code () {
    let url = "https://runjava.mcv.kr/runCode";
    let body;
    result.textContent = "실행 중 . . .";

    if(input_box.value !== ""){
        url += "WithInput"
        body = JSON.stringify({"code":editor.getValue(),"input":input_box.value})
    }
    else {
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