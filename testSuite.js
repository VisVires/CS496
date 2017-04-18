
// 1. Getting public Gists returns 30 Gists (you can get more via pagination, but you just need to get the first 30 and confirm that there are 30)
var data = JSON.parse(responseBody);
tests["Public Gists returns 30 Gists"] = data.length === 30;

// 2. Confirm that the user `wolfordj` has at least one public Gist
var data = JSON.parse(responseBody);
var result = false;
for (var i = 0; i < data.length; i++){
    if (data[i].owner.login == "wolfordj"){
        result = "true";
        break;
    }    
}
tests["wolfordj Has at least one public Gist"] = result == "true";

// 3. Confirm that when you create a Gist the number of Gists associated to your account increases by 1
//check total gists
var data = JSON.parse(responseBody);
var dataLength = data.length;
postman.setEnvironmentVariable("preCreateLength", dataLength);
tests["Status code is 200"] = responseCode.code === 200;
console.log(postman.getEnvironmentVariable("preCreateLength"));

//add gist
tests["Status code is 201"] = responseCode.code === 201;

//check total and compare to previous total
var data = JSON.parse(responseBody);
var dataLength = data.length;
var preCreateLength = postman.getEnvironmentVariable("preCreateLength");
console.log(preCreateLength);
tests["Status code is 200"] = responseCode.code === 200;
var postCreateLength = parseInt(preCreateLength) + 1;
console.log(dataLength);
console.log(postCreateLength);
tests["Added item to Gists"] = dataLength === postCreateLength;

// 4. Confirm that the contents of the Gist you created match the contents you sent

//test for correct response
tests["Status code is 200"] = responseCode.code === 200;
//check data for correct contents
var data = JSON.parse(responseBody);
var dataLength = data.length;
//check description
console.log(data[0].description);
tests["Description is Correct"] = data[0].description === "the description for this gist";
//check public variable
console.log(data[0].public);
tests["New Gist is public"] = data[0].public === true;
//check files and file contents
//console.log(Object.keys(data[dataLength-1].files));
var files = Object.keys(data[0].files)
tests["File name of added file is correct"] = files[0] === "file1.txt";

// 5. Confirm that you are able to edit the contents of a Gist (this will require editing it and proving the edits worked)

//check content of gist and get/store id
//check if GET is correct
tests["Status code is 200"] = responseCode.code === 200;

//parse data
var data = JSON.parse(responseBody);

//get data length
var dataLength = data.length;

//get id
var id = data[0].id;
//console.log(id);

//store id as enviromental variable
postman.setEnvironmentVariable("lastID", id);

//check description to be edited
//store value as enviromental variable
var descript = data[0].description;
//console.log(descript);
postman.setEnvironmentVariable("originalDescript", descript);

//change content of gist
//verify content of gist
//check if response is ok
tests["Status code is 200"] = responseCode.code === 200;
//get new description
var data = JSON.parse(responseBody);
var newDescript = data.description;
//get old Description
var oldDescript = postman.getEnvironmentVariable("originalDescript");
//compare descriptions
tests["Description was modified/edited using PATCH"] = newDescript !== oldDescript;

// 6. Confirm that you can add a star to a Gist
//GET list of starred gists

//GET #1
tests["Star not found so status code is 404"] = responseCode.code === 404;
//PUT
tests["Status code is 204, meaning star added"] = responseCode.code === 204;
//GET #2
tests["GET to check if star added confirmed, 204 Returned"] = responseCode.code === 204;

// 7. Confirm that your list of Starred gists is correct
tests["List of starred Gists returned"] = responseCode.code === 200;
var list = JSON.parse(responseBody);
postman.setEnvironmentVariable("listOfStarred", list);

// 8. Confirm you can remove a star from a Gist
//remove star
var lastID = postman.getEnvironmentVariable("lastID");
//check list for removed star
tests["List of starred Gists returned"] = responseCode.code === 200;
var list = JSON.parse(responseBody);
var lastID = postman.getEnvironmentVariable("lastID");
var result = false;
for (var i = 0; i < list.length; i++){
    if (list[i].id == lastID){
        result = "true";
    }
}
tests["Previously starred Gist is no longer in list of starred gists"] = result === false;

// 9. Confirm you can delete a Gist

