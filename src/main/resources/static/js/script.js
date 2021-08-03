console.log("This is script file")
const toggleSidebar=()=> {

    if($(".sidebar").is(":visible")) {
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","1%");


    } else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");

    }
}

function deleteContact(cid)
{
swal({
  title: "Are you sure?",
  text: "you want to delete this conatct.....",
  icon: "warning",
  buttons: true,
  dangerMode: true,
})
.then((willDelete) => {
  if (willDelete) {
		window.location="/user/delete/"+cid;
  } else {
    swal("Your contact is safe!");
  }
});
};

const dttime=()=>{
	console.log("date time running...........");
var dt = new Date();
document.getElementById('date-time').innerHTML=dt;
};

const search = () => {
	 //console.log("searching......");
	let query=$("#search-input").val();
	if(query==""){
	$(".search-result").hide();	
	}else{
	console.log(query);
	
	let url=`http://localhost:8080/search/${query}`;
	fetch(url).then((response) => {
		return response.json();
	}).then((data)=>{
		
		console.log(data);	
		let text =`<div class='list-group'>`
		data.forEach((contact)=>{
			text+=`<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'>${contact.name}</a>`
			
		});
		text+=`</div>`;
		$(".search-result").html(text);
		$(".search-result").show();	
		});
	
	//sending request to server
	}
};