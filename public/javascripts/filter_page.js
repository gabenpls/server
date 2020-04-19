; $(function() {
    $("#gp-achievements-filter").change(function() {
        const games = $("#gp-game-select option:selected").toArray().map(e => parseInt(e.value));
        $.ajax({
            method: 'POST',
            url: '/api/achievements/filter',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                games: games
            }),
            processData: false
        }).done(msg => {
            console.log(msg);
        });
    });
});