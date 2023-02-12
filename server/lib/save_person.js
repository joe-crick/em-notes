const fs = require("fs");
const filePath = "./server/data/people/";

module.exports = function savePerson(person) {
    const personId = person["person-id"];
    const fullPath = `${filePath}/${personId}.json`;  

  try {
    fs.writeFile(fullPath, jsonFormat(person), err => {
        if (err) {
          console.error(err);
        }
      });
  } catch (error) {
    console.log(error);
  }
};