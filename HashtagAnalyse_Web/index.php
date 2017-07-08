<!-- DBS 2017 -->
<!-- Gruppe 4 -->
<!-- Andreas Timmermann -->
<!-- Alena Dudarenok -->
<?php 
  $Hashtag = $_POST["Hashtag"];
//  echo $Hashtag;
//  echo "<br />\n";
  $counts = array();
  $dates = array();
  $countsSpecial = array();
  $datesSpecial = array();
  $dbconn = pg_connect("host=localhost port=5432 dbname=Election user=postgres password=postgres");
  $result = pg_query($dbconn, "SELECT count(timestamp::TIMESTAMP::DATE),timestamp::TIMESTAMP::DATE FROM genutztam GROUP BY timestamp::TIMESTAMP::DATE ORDER BY timestamp::TIMESTAMP::DATE ASC");
  if (!$result) 
  {
    echo "An error occurred.\n";
    exit;
  }

  $init = 0;
  while ($row = pg_fetch_row($result)) 
  {
//    echo "Count: $row[0]  Date: $row[1]";
    $t1 = strtotime($row[1]);
    if($init == 0)
    {
      $t2 = strtotime("2016-01-04");  
      $init = 1;
    }
    else
      $t2 = $last_date;

    $te = $t1 - $t2;
    $te = floor($te / (60 * 60 * 24));
    if($te > 1)
    {
      $t3 = $t2;
      for($i = 1;$i < $te;$i++)
      {
        $t3 += (60 * 60 * 24);
        $tp = Date("Y-m-d",$t3);
        $counts[] = 0;
        $dates[] = $tp;
      }
    }

    $counts[] = $row[0];
    $dates[] = $row[1];
    $last_date = $t1;
//    echo "<br />\n";
  }

  $result = pg_query($dbconn, "SELECT count(timestamp::TIMESTAMP::DATE),timestamp::TIMESTAMP::DATE FROM genutztam WHERE name='$Hashtag' GROUP BY timestamp::TIMESTAMP::DATE ORDER BY timestamp::TIMESTAMP::DATE ASC");
  if (!$result) 
  {
    echo "An error occurred.\n";
    exit;
  }

  $init = 0;
  while ($row = pg_fetch_row($result)) 
  {
    $t1 = strtotime($row[1]);
    if($init == 0)
    {
      $t2 = strtotime("2016-01-04");  
      $init = 1;
    }
    else
      $t2 = $last_date;
    $te = $t1 - $t2;
    $te = floor($te / (60 * 60 * 24));
    if($te > 1)
    {
      $t3 = $t2;
      for($i = 1;$i < $te;$i++)
      {
        $t3 += (60 * 60 * 24);
        $tp = Date("Y-m-d",$t3);
        $countsSpecial[] = 0;
        $datesSpecial[] = $tp;
//        echo "Count: 0  Date: $tp";
//        echo "<br />\n";
      }
    }
    $ts = Date("Y-m-d",$t1);
//    echo "Count: $row[0]  Date: $ts";
    $countsSpecial[] = $row[0];
    $datesSpecial[] = $ts;
    $last_date = $t1;
//    $counts[] = $row[0];
//    $dates[] = $row[1];
//  echo "<br />\n";
  }
  $t1 = strtotime("2016-09-28");
  $t2 = $last_date;
  $te = $t1 - $t2;
  $te = floor($te / (60 * 60 * 24));
  if($te > 1)
  {
    $t3 = $t2;
    for($i = 1;$i < $te;$i++)
    {
      $t3 += (60 * 60 * 24);
      $tp = Date("Y-m-d",$t3);
      $countsSpecial[] = 0;
      $datesSpecial[] = $tp;
//      echo "Count: 0  Date: $tp";
//      echo "<br />\n";
    }
  }
?>
<!DOCTYPE html>
<html>
  <head>
  <meta charset="utf-8"></meta>
  <title>Apple Aktie</title>
  <link href="my.css" rel="stylesheet" type="text/css">
  <script language="javascript" type="text/javascript" src="./libs/jquery.js"></script>
  <script language="javascript" type="text/javascript" src="./libs/jquery.flot.js"></script>
  <script language="javascript" type="text/javascript" src="./libs/jquery.flot.categories.js"></script>
  <script type="text/javascript">
  $(function() {

    
    var data = [];
    /* = [["02.01.17",2330417],["03.01.17",882614],["04.01.17",449615],["05.01.17",746100],
                ["06.01.17",700163],["09.01.17",1297576],["10.01.17",1691504],["11.01.17",1521071],
                ["12.01.17",1073743],["13.01.17",1149538],["16.01.17",861942],["17.01.17",848050],
                ["18.01.17",528968],["19.01.17",778900],["20.01.17",610841],["23.01.17",366744],
                ["24.01.17",734456],["25.01.17",1221101],["26.01.17",1302214],["27.01.17",739897],
                ["30.01.17",1268146],["31.01.17",988174]];
*/
      var y = $.parseJSON('<?php echo json_encode($counts); ?>');
      var d = $.parseJSON('<?php echo json_encode($dates); ?>');
      $i = 0;
      for(i = 0;i < y.length;i++)
      {
        var t = [];
        t.push(d[i]);
        t.push(y[i]);
        data.push(t);//daten;
//        console.log(t[0] + " , " + t[1]);
      }
    $.plot("#barchart", [ data ], {
      series: {
        bars: {
          show: true,
          barWidth: 0.1,
          align: "center"
        }
      },
      xaxis: {
          show: false,
        mode: "categories",
        tickLength: 0
//        ticks: 10
      }
    });    

    var dataS = [];
      var yS = $.parseJSON('<?php echo json_encode($countsSpecial); ?>');
      var dS = $.parseJSON('<?php echo json_encode($datesSpecial); ?>');
      $i = 0;
      for(i = 0;i < yS.length;i++)
      {
        var t = [];
        t.push(dS[i]);
        t.push(yS[i]);
        dataS.push(t);//daten;
//        console.log(t[0] + " , " + t[1]);
      }
    $.plot("#barchartSpecial", [ dataS ], {
      series: {
        bars: {
          show: true,
          barWidth: 0.1,
          align: "center"
        }
      },
      xaxis: {
          show: false,
        mode: "categories",
        tickLength: 0
//        ticks: 10
      }
    });    

  });
  </script>
  </head>
  <body>
    <div style="text-align:center">
      <h2> <?php echo "Hashtaganalyse"; ?> </h2>
    </div>
    <div id="content">
      <div class="demo-container">
      <h3> <?php echo "Gesamtanzahl der Hashtags (2016-01-05 bis 2016-09-27)" ?> </h3>
        <div id="barchart" class="demo-placeholder"></div>
      </div>
    </div>
    <form method="post">
      <div style="text-align:center">
        <label>
         <p>Hashtags: (sortiert nach der Gesamtanzahl)</p>
          <select name="Hashtag" size="5" onchange="this.form.submit()">
            <?php
              $dbconn = pg_connect("host=localhost port=5432 dbname=Election user=postgres password=postgres");
              $result = pg_query($dbconn, "SELECT name FROM hashtag ORDER BY haeufigkeit_gesamt DESC");
              if (!$result) {
                echo "An error occurred.\n";
                exit;
              }
              while ($row = pg_fetch_row($result)) {
                echo "<option value='$row[0]'>$row[0]</option>";
              }
            ?>
          </select>
        </label>
      </div>
    </form>
    <div id="content">
      <div class="demo-container">
      <h3> <?php echo "Hashtag: $Hashtag (2016-01-05 bis 2016-09-27)"; ?> </h3>
        <div id="barchartSpecial" class="demo-placeholder"></div>
      </div>
    </div>
  </body>
</html>

