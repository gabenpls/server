; $(function() {
    $("#gp-achievements-filter").change(function() {
        const games = $("#gp-game-select option:selected").toArray().map(e => e.value);
        $.ajax({
            method: 'POST',
            url: '/api/achievements/filter',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8;',
            data: JSON.stringify({
                games: games
            })
        }).done(msg => {
            console.log(msg);
        });
    });
});