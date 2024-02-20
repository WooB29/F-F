function startSpinner() {
  $("#spinner").css("display", "block");
  $("body").css("pointer-events", "none");
}

function stopSpinner() {
  $("#spinner").css("display", "none");
  $("body").css("pointer-events", "auto");
}