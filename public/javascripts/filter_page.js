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
            const container = $("#gp-filter-result");
            container.empty();
            const elems = msg.achievements.map(renderAchievement);
            elems.forEach(e => container.append(e));
        });
    });
});

function renderAchievement(ach) {
    return `<div class="box gp-achievement">
    <article class="media">
        <div class="media-left">
            <figure class="image is-64x64">
                <img src=${ ach.isAchieved ? ach.iconUrl : ach.iconUrlGray }>
            </figure>
        </div>
        <div class="media-content">
            <div class="content">
                <h6 class="title is-6">
                    ${ ach.title }
                </h6>
                <p>
                    ${ ach.description }
                </p>
                <div class="gp-complection-persent">
                    ${ ach.percent }%
                </div>
            </div>
        </div>
        <div class="media-right">
            <img src="${ ach.game.iconUrl }" title="${ ach.game.name }">
        </div>
    </article>
</div>`;
}