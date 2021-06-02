const url = `/api/game_view/${getParameterByName("gp")}`;

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
      console.log(error);
    });
}

function getParameterByName(name) {
  let match = RegExp("[?&]" + name + "=([^&]*)").exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, " "));
}

function updateView(data) {
  let playerInfo;
  if (data.gamePlayers[0].id == getParameterByName("gp"))
    playerInfo = [data.gamePlayers[0].player, data.gamePlayers[1].player];
  else playerInfo = [data.gamePlayers[1].player, data.gamePlayers[0].player];

  $("#playerInfo").text(
    playerInfo[0].email + "(you) vs " + playerInfo[1].email
  );

  data.ships.forEach(function (shipPiece) {
    shipPiece.locations.forEach(function (shipLocation) {
      if (isHit(shipLocation, data.salvoes, playerInfo[0].id) != 0) {
        $("#B_" + shipLocation).addClass("ship-piece-hited");
        $("#B_" + shipLocation).text(
          isHit(shipLocation, data.salvoes, playerInfo[0].id)
        );
      } else $("#B_" + shipLocation).addClass("ship-piece");
    });
  });
  data.salvoes.forEach(function (salvo) {
    if (playerInfo[0].playerId === salvo.player) {
      salvo.locations.forEach(function (location) {
        $("#S_" + location).addClass("salvo-piece");
        $("#S_" + location).text(salvo.turn);
      });
    } else {
      salvo.locations.forEach(function (location) {
        $("#B_" + location).addClass("salvo");
      });
    }
  });

}

function isHit(shipLocation, salvoes, playerId) {
  let turn = 0;
  salvoes.forEach(function (salvo) {
    if (salvo.player != playerId)
      salvo.locations.forEach(function (location) {
        if (shipLocation === location) turn = salvo.turn;
      });
  });
  return turn;
}