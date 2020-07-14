var runesSound = new Audio();
var roshSound = new Audio();
var myTimeout;

runesSound.src = "/assets/sounds/John_Cena.mp3"

roshSound.src = "/assets/sounds/roshan.mp3"

function setRunesAlarm() {
    sleep(60000);
    setInterval(initRunesAlarm, 240000);
}

function setRoshAlarm() {
    clearTimeout(myTimeout);
    myTimeout = setTimeout(() => {roshSound.play()}, 300000);
    myTimeout = setTimeout(() => {roshSound.play()}, 540000);
    myTimeout = setTimeout(() => {roshSound.play()}, 600000);
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




