const fs = require("fs");
const filePath = "./server/data/people/";

module.exports = function getPeople() {
  const people = [];
  const files = fs.readdirSync(filePath);

  try {
    for (const file of files) {
      const fullPath = `${filePath}/${file}`;
      const data = fs.readFileSync(fullPath, "utf-8");
      const person = JSON.parse(data);
      people.push(person);
    }
    return people;
  } catch (error) {
    console.log(error);
  }
};