ngDefine('cockpit.plugin.wbs-plugin.servicesModule', function(module){
	
	module.factory('DataFactory', ["$http", "Uri", "$rootScope", "$q", function($http, Uri, $rootScope, $q){
		var DataFactory ={};
		
		DataFactory.processDefinitions = [];
		
		DataFactory.getAllProcessDefinitions = function(){
			return $http.get(Uri.appUri("plugin://wbs-plugin/:engine/process-definition"))
		      .success(function(data) {
		        DataFactory.processDefinitions = data;
		     });
		};
		
		DataFactory.getProjectPlanNames = function(){
			return $http.get(Uri.appUri("plugin://wbs-plugin/:engine/getProjectPlanNames"))
			  .success(function(data){
					
			});
		};
		
		DataFactory.getTasks = function(plano){
			console.log(plano);
			return $http.get(Uri.appUri("plugin://wbs-plugin/:engine/tasks?planoProjeto="+plano))
			.success(function(data){
			});
		};
		
		DataFactory.criarPlanoDeProjeto = function(plano){
			return $http.get(Uri.appUri("plugin://wbs-plugin/:engine/createProjectPlan?IdProcesso=" + plano.IdProcesso + "&NomePlano=" + plano.Nome))
			.success(function(data){
				
			});
		}
		
		DataFactory.createTask = function(plano){
			return $http.post(Uri.appUri("plugin://wbs-plugin/:engine/createTask/" + plano.Nome +"?taskName=" + plano.taskName + "&taskInicio="  + plano.taskInicio + "&taskFim=" + plano.taskFim + "&taskTipo=" + plano.taskTipo ))
			.success(function(data){
				
			});
		}
		
		DataFactory.deleteTask= function(plano){
			return $http.post(Uri.appUri("plugin://wbs-plugin/:engine/deleteTask/" + plano.Nome + "?taskId=" + plano.taskId ))
			.success(function(data){
				
			});
		}
		
		DataFactory.validate = function(){
			return $http.get(Uri.appUri("plugin://wbs-plugin/:engine/validate/" + NomePlano))
			.success(function(data){
				
			});
		}
		
		return DataFactory;
	}]);
});