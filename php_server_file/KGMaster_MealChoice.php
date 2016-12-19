<?php

$connect = mysqli_connect("localhost","junsueg5737","a741236985b","junsueg5737");
if(mysqli_connect_errno())
    echo "ERROR : " .mysqli_connect_error();
$meal_time   = $_POST["Time"];
$meal_choice = $_POST["Choice"];
$meal_date   = $_POST["Date"];

echo "$meal_date";
echo "$meal_time"
echo "$meal_choice"

$query = mysqli_query($connect,"SELECT * FROM KGMealData WHERE Today = '$meal_today' AND Time = '$meal_time'");

// 급식 투표율만 볼 때
if(meal_choice == "None")
{

}
else
{

}
?>