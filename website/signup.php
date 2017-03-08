<?php
    session_start();
    
    //Extract submitted username and password.
    $username = $_POST['username'];
    $submitted_password = $_POST['password'];
    $submitted_password1 = $_POST['password1'];
    
    // Check for invalid data.
    if (!isset($username) or !isset($submitted_password) or !isset($submitted_password1)) {
        exit();
    }
    
    else if ($submitted_password != $submitted_password1) {
        exit();
    }
    
    // Connect to database
    try {
        $dbh = new PDO("mysql:host=localhost;dbname=website", "root", NULL);
    } catch (PDOExecption $e) {
        exit('Database connection failed: ' . $e->getMessage());
    }
    
    // Retrieve the actual password for given user
    $stmt = $dbh->prepare("SELECT username FROM users WHERE username = :username"); // Sets statement for mysql, increments counter when page is viewed
    $stmt->bindParam(':username', $username);
    $stmt->execute() or exit("SELECT failed."); // Executes $stmt
    
    // If there is no such user, then redirect to login page.
    if ($stmt->rowCount() != 0) {
        exit();
    }
    
    $stmt = $dbh->prepare("INSERT INTO users VALUES (:username, :submitted_password);"); // Sets statement for mysql, increments counter when page is viewed
    $stmt->bindParam(':username', $username);
    $stmt->bindParam(':submitted_password', $submitted_password);
    $stmt->execute() or exit("SELECT failed."); // Executes $stmt
   
   
    
    // Log the user in.
    $_SESSION["username"] = $username;
    
    // Redirect to home page
    header('Location: ./index.html');

?>