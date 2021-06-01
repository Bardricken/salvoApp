const urlParams = new URLSearchParams(window.location.search);
const url = `/api/game_view/${urlParams.get("gp")}`;

loadData();

function loadData() {
  fetch(url)
    .then((resp) => resp.json())
    .then((data) => {
      console.log(data);
      updateView(data);
    })
    .catch((error) => {
      alert("No se ha encontrado el juego!");
    });
}

function updateView(game) {
  let item = document.createElement("tr");
  let item2 = document.createElement("td");
  item2.appendChild(document.createTextNode(" "));
  item.appendChild(item2);
  let res = [];
  game.ships.forEach(function (dt) {
    res = res.concat(dt.locations.map((s) => s.split("")));
  });
  for (let i = 1; i <= 10; i++) {
    item2 = document.createElement("td");
    item2.appendChild(document.createTextNode(i));
    item.appendChild(item2);
  }
  document.getElementById("table1").appendChild(item);
  for (let y = 1; y <= 10; y++) {
    item = document.createElement("tr");
    item2 = document.createElement("td");
    item2.appendChild(
      document.createTextNode((y + 9).toString(36).toUpperCase())
    );
    item.appendChild(item2);
    for (let x = 1; x <= 10; x++) {
      item2 = document.createElement("td");
      res.forEach(function (ele) {
        if (
          ele[0].localeCompare((y + 9).toString(36).toUpperCase()) == 0 &&
          ele[1].localeCompare(x.toString()) == 0
        ) {
          item2.appendChild(document.createTextNode("F"));
          item2.style.backgroundColor = "#0000FF";
        } else {
          item2.appendChild(document.createTextNode(" "));
        }
      });
      item.appendChild(item2);
    }
    document.getElementById("table1").appendChild(item);
  }
  document.getElementById("para").innerHTML = game.gamePlayers
    .map(function (p) {
      let aux = p.player.email;
      if (0 == urlParams.get("gp").localeCompare(p.gamePlayerId.toString())) {
        aux = aux.concat(" (YOU)");
      }
      return aux;
    })
    .join(" vs ");
}
