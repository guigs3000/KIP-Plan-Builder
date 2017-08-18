ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module) {
  module.controller('createPlanController', ['$scope','$modalInstance', 'DataFactory',function($scope, $modalInstance, DataFactory) {
	  $scope.processDefinitions = [];
	  $scope.processoSelecionado = "";
	  var init = function(){
		  DataFactory.getAllProcessDefinitions().then(function(response){
			  $scope.processDefinitions = response.data;
			  console.log(response.data);
		  })
	  }
	  
	  init();

	  $scope.save= function() {
		  var idProcesso = $("#processDefinition").val();
		  var nomePlano = $("#nomePlano").val();
		  var obj = { Nome: nomePlano, IdProcesso: idProcesso};
		  DataFactory.criarPlanoDeProjeto(obj);
		  $modalInstance.close();
	  }

	  $scope.cancel = function() {
		  $modalInstance.dismiss('cancel');
	  }
    
    
  }])
});