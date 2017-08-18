ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module){
	
	module.controller('dashboardController', ['$scope', '$rootScope','DataFactory',  function($scope, $rootScope, DataFactory){
		
		$rootScope.$on("planoSelecionado", function(event, data){
			$("#processTab").hide();
			$("#tasksTab").show();
		});
		
		$rootScope.$on("home", function(event, data){
			$("#tasksTab").hide();
			$("#processTab").show();
		});
		 
	}]);
});