var alarmSound = new Audio();
var sec = 0;
alarmSound.src = "/assets/sounds/John_Cena.mp3"

function setAlarm() {
    setInterval(initAlarm, 5000);
setInterval( function(){
        $("#seconds").html(pad(++sec%60));
        $("#minutes").html(pad(parseInt(sec/60,10)));
    }, 1000);
}
function initAlarm(){
    alarmSound.play();
}

function pad ( val ) {
 return val > 9 ? val : "0" + val;
 }



