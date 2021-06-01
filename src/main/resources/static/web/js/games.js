const gameList = document.querySelector("#game-list");
const url = `/api/games`;

loadData();

function loadData() {
  fetch(url)
    .then((resp) => resp.json())
    .then((data) => {
      console.log(data);
      updateView(data);
    });
}

function updateView(data) {
  let htmlList = data
    .map((game) => {
      return `<li> ${new Date(
        game.created
      ).toLocaleString()} ${game.gamePlayers.map((p) => {
        return p.player.email;
      })} </li>`;
    })
    .join("");
  gameList.innerHTML = htmlList;
}
