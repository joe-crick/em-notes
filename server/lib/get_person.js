const fs = require("fs");
const filePath = "./server/data/people/";

module.exports = function getPerson(personId) {
  const fullPath = `${filePath}/${personId}.json`;

  try {
    const person = fs.readFileSync(fullPath);
    return JSON.parse(person);
  } catch (error) {
    console.log(error);
  }
};