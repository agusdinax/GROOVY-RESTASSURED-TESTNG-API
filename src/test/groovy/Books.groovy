import io.restassured.response.Response
import org.testng.Assert
import org.testng.annotations.Test

import static io.restassured.RestAssured.*
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClassPath

class Books extends Base{
    @Test(groups="smoke")
    void getBooksList(){
        Response response = get ("/books")
        ArrayList<String> allBooks = response.path ("data.title")
        Assert.assertTrue(allBooks.size()>1, "No books returned")
    }
    @Test
    void booksSchemaIsValid(){
        get("/books")
        .then()
        .assertThat()
        .body(matchesJsonSchemaInClassPath("booksSchema.json"))
    }
    @Test
    void createBookandDeleteBook(){
        File bookFile = new File(getClass().getResource("/book.json").toURI())

        Response createResponse = 
        given()
            .body (bookFile)
            .when()
            .post("/books");
        String responseID = createResponse.jsonPath().getString("post.book_id")

        Assert.assertEquals(deleteResponse.getStatusCode(), 201)

        Response deleteResponse =
        given()
        .body ("{\n" +
                    "\t\"book_id\": " + responseID + "\n" +
                    "} ")
        .when()
        .delete("/books/{bookId}", responseID);


        Assert.assertEquals(deleteResponse.getStatusCode(), 200)
        Assert.assertEquals(deleteResponse.jsonPath.getString("message"), "Book succesfuly deleted")
    }

    @Test
    void deleteNonExistentBook_FailMessage(){
        String nonExistentBookID = "6565656"

        Response deleteResponse =
        given()
                .body("{\n" +
                         "\t\"book_id\":"+nonExistentBookID+"\n"+
                         "}")
                .when()
                .delete("/books")

        Assert.assertEquals(deleteResponse.getStatusCode(), 500)
        Assert.assertEquals(deleteResponse.jsonPath.getString("error"), "Unable to find book id: " + nonExistentBookID)
    }
}