<?php
    session_start();
    
    //Extract submitted username and password.
    $username = $_POST['username'];
    $submitted_password = $_POST['password'];
    
    // Check for invalid data.
    if (!isset($username) or !isset($submitted_password)) {
        header('Location: ./login.html');
        exit();
    }
    
    // Connect to database
    try {
        $dbh = new PDO("mysql:host=localhost;dbname=/* *?", "root", NULL); // Need to change this to SQLite database for online storage
    } catch (PDOExecption $e) {
        exit('Database connection failed: ' . $e->getMessage());
    }
    
    // Retrieve the actual password for given user
    $stmt = $dbh->prepare("SELECT password FROM users WHERE username = :username"); // Sets statement for mysql, increments counter when page is viewed
    $stmt->bindParam(':username', $username);
    $stmt->execute() or exit("SELECT failed."); // Executes $stmt
    
    // If there is no such user, then redirect to login page.
    if ($stmt->rowCount() == 0) {
        header('Location: ./login.html');
        exit();
    }
   
   // Extract the actual password
    $row = $stmt->fetch() or exit("fetch failed.");
    $actual_password = $row["password"];
    
    // If the submitted password does not match the actual password,
    // then redirect to login page.
    if ($submitted_password != $actual_password) {
       header('Location: ./login.html');
       exit();
    }
    
    // Log the user in.
    $_SESSION["username"] = $username;
    
    // Redirect to home page
    header('Location: ./index.html');

?>