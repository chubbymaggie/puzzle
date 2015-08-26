google.load('visualization', '1.1', {packages: ['line', 'corechart']});
google.setOnLoadCallback(drawChart);

    function drawChart() {
      var syntaxChart;
      var syntaxDiv = document.getElementById('chart-maitenance-costs-syntax');
      var data = new google.visualization.DataTable()
      data.addColumn('number', "Amount of constructs");
      data.addColumn('number', "Family approach");
      data.addColumn('number', "Product line approach");

      data.addRows([
        [0,  0.0,  0], 
        [6,  16.5,  12], 
        [12,  33.0,  24], 
        [18,  49.5,  36], 
        [24,  66.0,  48], 
        [30,  82.5,  60], 
        [36,  99.0,  72], 
        [42,  115.5,  84], 
        [48,  132.0,  96], 
        [54,  148.5,  108]      ]);

      var syntaxOptions = {
        title: 'Maintenance costs of the family of DSLs vs. maintenance costs of its corresponding language product line (abstract syntax)',
        titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
        width: 650,
        height: 280,
        series: {
          0: {targetAxisIndex: 0}
        },
        vAxes: {
          0: {title: 'Maintenance Costs (Man-Hour)',
              titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
              textStyle: {fontSize: 10, fontName: "lucida sans unicode", bold: true }
             }
        },
        hAxis: {
          title: 'Amount of Involved Constructs',
          titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
          textStyle: {fontSize: 10, fontName: "lucida sans unicode", bold: true },
          ticks: [0, 6, 12, 18, 24, 30, 36, 42, 48, 54]
        },
        legend: { position: 'top',
      			  textStyle: {fontSize: 10, fontName: "lucida sans unicode" }
    			}
      	};
      var semanticsChart;
      var semanticsDiv = document.getElementById('chart-maitenance-costs-semantics');
      var semanticsData = new google.visualization.DataTable()
      semanticsData.addColumn('number', "Amount of methods");
      semanticsData.addColumn('number', "Family approach");
      semanticsData.addColumn('number', "Product line approach");

      semanticsData.addRows([
        [0,  0.0,  0],
        [8,  17.513513513513512,  16],
        [16,  35.027027027027025,  32],
        [24,  52.54054054054053,  48],
        [32,  70.05405405405405,  64],
        [40,  87.56756756756756,  80],
        [48,  105.08108108108107,  96],
        [56,  122.59459459459458,  112],
        [64,  140.1081081081081,  128],
        [72,  157.6216216216216,  144],
      ]);

      var semanticsOptions = {
        title: 'Maintenance costs of the family of DSLs vs. maintenance costs of its corresponding language product line (semantics)',
        titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
        width: 650,
        height: 280,
        series: {
          0: {targetAxisIndex: 0}
        },
        vAxes: {
          0: {title: 'Maintenance Costs (Man-Hour)',
              titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
              textStyle: {fontSize: 10, fontName: "lucida sans unicode", bold: true }
             }
        },
        hAxis: {
          title: 'Amount of Involved Methods',
          titleTextStyle: {fontSize: 10, fontName: "lucida sans unicode" },
          textStyle: {fontSize: 10, fontName: "lucida sans unicode", bold: true },
          ticks: [0, 8, 16, 24, 32, 40, 48, 56, 64, 72]
        },
        legend: { position: 'top',
      			  textStyle: {fontSize: 10, fontName: "lucida sans unicode" }
    			}
      	};
      syntaxChart = new google.visualization.LineChart(syntaxDiv);
      syntaxChart.draw(data, syntaxOptions);
      syntaxChart = new google.visualization.LineChart(semanticsDiv);
      syntaxChart.draw(semanticsData, semanticsOptions);
    }
