ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module) {
  
	module.controller('createTaskCtrl', ['$scope','$modalInstance', 'planoProjeto', 'DataFactory', '$filter', '$rootScope', function($scope, $modalInstance, planoProjeto, DataFactory, $filter, $rootScope) {
	  
	  
	  var init = function(){
		  $scope.tiposTarefa = planoProjeto.possibleTasks;
	  };
		
	  init();
    
	  $scope.save = function() {
		  $modalInstance.close();
		  var taskTipo = $("#tipoTarefa").val();
		  var taskNickname = $filter("filter")($scope.tiposTarefa, {id: taskTipo})[0].name;
		  var taskInicio = $("#dataPlanejadaInicio").val();
		  var taskFim = $("#dataPlanejadaFim").val();
		  var taskNome = $("#nomeTarefa").val();
		  var plano = {"Nome": planoProjeto.name, "taskName": taskNome, "taskTipo": taskTipo, "taskInicio": taskInicio, "taskFim": taskFim, "nickname": taskNickname};
		  DataFactory.createTask(plano).then(function(response){
			  DataFactory.getTasks(plano.Nome).then(function(response){
					$rootScope.$emit("planoSelecionado", response.data);
				});
		  });
	  };
    
	  $scope.cancel = function() {
		  $modalInstance.dismiss('cancel');
	  };
    
    
	}])
});