ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module){
	
	module.controller('tasksController', ['$rootScope', "$scope", "DataFactory", "$modal", function($rootScope, $scope, DataFactory, $modal){
		
		//Plano de projeto selecionado pelo usuario.
		$scope.PlanoProjeto = {};
		
		//Ao receber evento, carrega os dados na tela
		$rootScope.$on("planoSelecionado", function(event, data){
			$scope.PlanoProjeto = data;
			console.log(data);
		});
		
		//Quando usuario clicar em Home, emite evento para voltar a tela inicial
		$scope.home = function(){
			$rootScope.$emit("home",  true);
		};
		
		//Abre popup para criacao de tarefa
		$scope.openCriarTarefa = function(){
			var modalInstance = $modal.open({
				templateUrl: 'createTask',
				controller: 'createTaskCtrl',
				size: 'lg',
				resolve: {
					planoProjeto: function(){
						return $scope.PlanoProjeto;
					}
				}
			});

			modalInstance.result.then(function () {

			}, function () {

			});
		};
		
		$scope.deleteTask = function(nome, tarefaId){
			var obj = {"Nome": nome, "taskId": tarefaId};
			DataFactory.deleteTask(obj).then(function(response){
				DataFactory.getTasks(nome).then(function(response){
					$rootScope.$emit("planoSelecionado", response.data);
				});
			});
			
		};
		
		$scope.gerarRelatorio = function(NomePlano){
			DataFactory.validate(NomePlano).then(function(response){
				var modalInstance = $modal.open({
					templateUrl: 'relatorioPopup',
					controller: 'relatorioPopupCtrl',
					size: 'lg',
					resolve: {
						inconformidades: function(){
							return response.data;
						}
					}
				});
				modalInstance.result.then(function () {

				}, function () {

				});
				
				

			});
		};
		
		$scope.deploy = function(nomePlano){
			DataFactory.deploy(nomePlano);
		};
		
	}]);
});
