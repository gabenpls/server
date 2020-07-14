var runesSound = new Audio();
var roshSound = new Audio();


runesSound.src = "/assets/sounds/John_Cena.mp3"

roshSound.src = "/assets/sounds/roshan.mp3"

function setRunesAlarm() {
    sleep(60000);
    setInterval(initRunesAlarm, 240000);
}

function setRoshAlarm() {
    setTimeout(initRoshAlarm, 300000);
    setTimeout(initRoshAlarm, 180000);
    setTimeout(initRoshAlarm, 120000);
}

function initRunesAlarm(){
    runesSound.play();
}

function initRoshAlarm(){
    roshSound.play();
}

function sleep(milliseconds) {
  const date = Date.now();
  let currentDate = null;
  do {
    currentDate = Date.now();
  } while (currentDate - date < milliseconds);
}




