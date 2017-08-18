ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module){
	
	module.controller('processesController', ['$scope', 'DataFactory', '$rootScope', '$modal', function($scope, DataFactory, $rootScope, $modal){
		
		//Planos de projeto criados pelo usuario
		$scope.nomesPlanosDeProjeto= [];
		
		var init = function(){
			DataFactory.getProjectPlanNames().then(function(response){
				console.log(response.data);
				$scope.planosProjeto = response.data;
			});
		};
		
		init();
	
		//Usuario seleciona um plano para editar as tarefas.  
		$scope.carregarPlanoProjeto = function(name){
			DataFactory.getTasks(name).then(function(response){
				$rootScope.$emit("planoSelecionado", response.data);
			})
			
		};
		
		$scope.openCriarPlanoProjeto = function() {
			
	        var modalInstance = $modal.open({
	          templateUrl: 'createPlan',
	          controller: 'createPlanController',
	          size: 'lg'
	        });
	        
	        modalInstance.result.then(function (processKeysToFilter) {

	        }, function () {
	          
	        });

		};
		
	}]);
});