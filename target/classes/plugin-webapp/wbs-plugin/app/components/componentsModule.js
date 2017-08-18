ngDefine('cockpit.plugin.wbs-plugin.componentsModule', [
	'./dashboardController',
	'./processesController',
	'./tasksController',
	'./createPlanController',
	'./createTaskCtrl'
], 
function(module){
	
	var Configuration = ['ViewsProvider', function(ViewsProvider) {

	    ViewsProvider.registerDefaultView('cockpit.dashboard', {
	      id: 'process-definitions',
	      label: 'Deployed Processes',
	      url: 'plugin://wbs-plugin/static/app/dashboard.html',
	      dashboardMenuLabel: 'Sample',
	      controller: 'dashboardController',

	      // make sure we have a higher priority than the default plugin
	      priority: 12
	    });
	  }];

	  Configuration.$inject = ['ViewsProvider'];

	  module.config(Configuration);

	  return module;
});