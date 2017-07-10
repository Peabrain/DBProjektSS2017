<!-- DBS 2017 -->
<!-- Gruppe 4 -->
<!-- Andreas Timmermann -->
<!-- Alena Dudarenok -->
<?php 
  $Hashtag = $_POST["Hashtag"];   // Hashtag der im unteren Diagramm dargestellt werden soll

  $counts = array();              // Liste Anzahl der Hashtags pro Tag
  $dates = array();               // Liste der Daten
  $countsSpecial = array();       // Liste der Anzahl des ausgewählten Hashtags pro Tag
  $datesSpecial = array();        // Liste der Daten

  // Verbindung zum Server herstellen
  $dbconn = pg_connect("host=localhost port=5432 dbname=Election user=postgres password=postgres");

  // Abfrage aller Hashtags in der über den gesamten Zeitraum der in der Datenbank vorhanden ist
  // Rückgabe ist eine Liste mit Daten und Anzahl aller Hashtags
  $result = pg_query($dbconn, "SELECT count(timestamp::TIMESTAMP::DATE),timestamp::TIMESTAMP::DATE FROM genutztam GROUP BY timestamp::TIMESTAMP::DATE ORDER BY timestamp::TIMESTAMP::DATE ASC");
  if (!$result) 
  {
    echo "An error occurred.\n";
    exit;
  }

  // Abarbeitung aller Ergebnisse
  $init = 0;
  while ($row = pg_fetch_row($result)) 
  {
    // Letztes bearbeitetes Datum wird bei der Initialisierung auf 04.01.2016 gesetzt 
    $t1 = strtotime($row[1]);
    if($init == 0)
    {
      $t2 = strtotime("2016-01-04");  
      $init = 1;
    }
    else
      $t2 = $last_date;

    // Auffüllen der Tabelle vom letzten bearbeiteten Datum bis zum aktuellen Datum mit 0 Hashtagcounts 
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
    // Dann wird der aktuelle Eintrag in die Tabellen eingetragen
    $counts[] = $row[0];
    $dates[] = $row[1];
    $last_date = $t1;
  }

  // Serveranfrage für den ausgewählten Hashtag.
  $result = pg_query($dbconn, "SELECT count(timestamp::TIMESTAMP::DATE),timestamp::TIMESTAMP::DATE FROM genutztam WHERE name='$Hashtag' GROUP BY timestamp::TIMESTAMP::DATE ORDER BY timestamp::TIMESTAMP::DATE ASC");
  if (!$result) 
  {
    echo "An error occurred.\n";
    exit;
  }

  // Gleiches Spiel wie Oben
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
      }
    }
    $ts = Date("Y-m-d",$t1);
    $countsSpecial[] = $row[0];
    $datesSpecial[] = $ts;
    $last_date = $t1;
  }
  // Auffüllen der Hashtaganzahl bis zum 28.09.2016 mit Hashtagcount 0
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
    }
  }
?>
<!DOCTYPE html>
<html>
  <head>
  <meta charset="utf-8"></meta>
  <title>Datenbank Projekt SS2017</title>
  <link href="my.css" rel="stylesheet" type="text/css">
  <script language="javascript" type="text/javascript" src="./libs/jquery.js"></script>
  <script language="javascript" type="text/javascript" src="./libs/jquery.flot.js"></script>
  <script language="javascript" type="text/javascript" src="./libs/jquery.flot.categories.js"></script>
  <script type="text/javascript">
  $(function() 
  {        
    var data = [];
    // Holen der Tagen vom PHP Script
    var y = $.parseJSON('<?php echo json_encode($counts); ?>');
    var d = $.parseJSON('<?php echo json_encode($dates); ?>');
    $i = 0;
    // Tabelle auffüllen und Plotten
    for(i = 0;i < y.length;i++)
    {
      var t = [];
      t.push(d[i]);
      t.push(y[i]);
      data.push(t);
    }
    $.plot("#barchart", [ data ], 
    {
      series: 
      {
        bars: 
        {
          show: true,
          barWidth: 0.1,
          align: "center"
        }
      },
      xaxis: 
      {
        show: false,
        mode: "categories",
        tickLength: 0
      }
    });    

    var dataS = [];
    // Holen der Tagen vom PHP Script
    var yS = $.parseJSON('<?php echo json_encode($countsSpecial); ?>');
    var dS = $.parseJSON('<?php echo json_encode($datesSpecial); ?>');
    $i = 0;
    // Tabelle auffüllen und Plotten
    for(i = 0;i < yS.length;i++)
    {
      var t = [];
      t.push(dS[i]);
      t.push(yS[i]);
      dataS.push(t);//daten;
    }
    $.plot("#barchartSpecial", [ dataS ], 
    {
      series: 
      {
        bars: 
        {
          show: true,
          barWidth: 0.1,
          align: "center"
        }
      },
      xaxis: 
      {
        show: false,
        mode: "categories",
        tickLength: 0
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

