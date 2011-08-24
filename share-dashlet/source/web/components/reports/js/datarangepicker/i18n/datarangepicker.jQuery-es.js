var daterangepickerRegional =[];

daterangepickerRegional['es'] = {
		presetRanges: [
			{text: 'Hoy', dateStart: 'today', dateEnd: 'today' },
			{text: 'Últimos 7 días', dateStart: 'today-7days', dateEnd: 'today' },
			{text: 'Lo que va de mes', dateStart: function(){ return Date.parse('today').moveToFirstDayOfMonth();  }, dateEnd: 'today' },
			{text: 'Lo que va de año', dateStart: function(){ var x= Date.parse('today'); x.setMonth(0); x.setDate(1); return x; }, dateEnd: 'today' },
			{text: 'El mes pasado', dateStart: function(){ return Date.parse('1 month ago').moveToFirstDayOfMonth();  }, dateEnd: function(){ return Date.parse('1 month ago').moveToLastDayOfMonth();  } }
		], 
		presets: {
			specificDate: 'Fecha concreta', 
			allDatesBefore: 'Todas las fechas antes de', 
			allDatesAfter: 'Todas las fechas después de', 
			dateRange: 'Rango de fechas'
		},
		rangeStartTitle: 'Fecha inicio',
		rangeEndTitle: 'Fecha fin',
		nextLinkText: 'Siguiente',
		prevLinkText: 'Anterior',
		doneButtonText: 'Hecho',
		earliestDate: Date.parse('-15years'), //earliest date allowed 
		latestDate: Date.parse('+15years'), //latest date allowed 
		rangeSplitter: '-', //string to use between dates in single input
		dateFormat: 'dd/mm/yy' // date formatting. Available formats: http://docs.jquery.com/UI/Datepicker/%24.datepicker.formatDate
	};
