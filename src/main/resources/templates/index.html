<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registration Form</title>
</head>
<body>
<h2>Registration Form</h2>
<h3>Create new Users</h3>
<form id="registrationForm">
    gmail: <input type="text" name="gmail"/><br><br>
    Password: <input type="text" name="password"/><br><br>
    Surname: <input type="text" name="surname"/><br><br>
    Name: <input type="text" name="name"/><br><br>
    Number: <input type="number" name="phone_number"/><br><br>
    Role:
    <input type="radio" name="role" value="director"> Director
    <input type="radio" name="role" value="actor"> Actor
    <input type="radio" name="role" value="film_crew_member"> Film Crew Member<br><br>
    <input type="submit" value="Create Users"/>
</form>

<script>
    document.getElementById("registrationForm").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the form from submitting normally
        var formData = new FormData(this); // Create FormData object to store form data
        var xhr = new XMLHttpRequest(); // Create XMLHttpRequest object
        xhr.open("POST", "/user/create", true); // Open a new POST request to the specified URL
        xhr.onreadystatechange = function () {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 201) { // If request is successful (status code 201)
                    console.log("User created successfully!");
                    window.location.href = xhr.getResponseHeader("Location"); // Redirect to the created user's location
                } else {
                    console.error("Failed to create user: " + xhr.responseText); // Log error message if request fails
                }
            }
        };
        xhr.send(formData); // Send the form data
    });
</script>

</body>
</html>
