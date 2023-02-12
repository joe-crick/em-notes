const fs = require("fs");
const file = './server/data/app-db.json';

module.exports = function getPeople() {

  try {
    const data = fs.readFileSync(file);
    const db = JSON.parse(data);
    return Object.values(db.teams);
  } catch (error) {
    console.log(error);
  }
};