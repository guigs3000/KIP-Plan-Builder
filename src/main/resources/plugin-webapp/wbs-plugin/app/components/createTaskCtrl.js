ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module) {
  
	module.controller('createTaskCtrl', ['$scope','$modalInstance', 'planoProjeto', 'DataFactory', function($scope, $modalInstance, planoProjeto, DataFactory) {
	  
	  
	  var init = function(){
		  $scope.tiposTarefa = planoProjeto.possibleTasks;
	  };
		
	  init();
    
	  $scope.save = function() {
		  $modalInstance.close();
		  var taskTipo = $("#tipoTarefa").val();
		  var taskInicio = $("#dataPlanejadaInicio").val();
		  var taskFim = $("#dataPlanejadaFim").val();
		  var taskNome = $("#nomeTarefa").val();
		  var plano = {"Nome": planoProjeto.name, "taskName": taskNome, "taskTipo": taskTipo, "taskInicio": taskInicio, "taskFim": taskFim};
		  DataFactory.createTask(plano);
	  };
    
	  $scope.cancel = function() {
		  $modalInstance.dismiss('cancel');
	  };
    
    
	}])
});