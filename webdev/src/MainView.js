import React from 'react';
import ReactDOM from 'react-dom';

class ContentView extends React.Component{
	constructor(props){
 		super(props);           

	}

	render(){
		if(!this.props.connected){
			return <p>Status: {this.props.connected}</p>;
		}else if(this.props.connected && this.props.contactData == null){
			return <p>No Data present</p>;
		}else if(this.props.connected && this.props.contactData != null){
			console.log(this.props.contactData);
			return <p>Contact length: {this.props.contactData.length}</p>;
		}
	}

	componentWillMount(){

	}
}

class StatusButton extends React.Component{
	constructor(props){
 		super(props);           

	}

	render(){
		if(this.props.connected){
			return <button type="button" className="btn btn-success">Connected</button>;
		}else{
			return <button  type="button" className="btn btn-danger">not Connected</button>;
		}
	}

	componentWillMount(){
	
	}
}


class MenuView extends React.Component{
	constructor(props){
 		super(props);

	}

	render(){
		return(
			<div>
				<button type="button" className="btn btn-default" >Refresh</button>
				<StatusButton connected={this.props.connected} />
				<a href={this.props.authLink}>Connect</a>
			</div>
		)
	}

	componentWillMount(){
	
	}
}

export default class MainView extends React.Component{
	constructor(props){
 		super(props);
 		
 		this.state = {
 			connected: false,
 			authLink : null,
 			contactData: null
 		};           

 		this.handleAuthLinkRecieved = this.handleAuthLinkRecieved.bind(this);
 		this.handleConnectionStatusRecieved = this.handleConnectionStatusRecieved.bind(this);
 		this.handleContactData = this.handleContactData.bind(this);
	}

	render(){
		return(
			<div className="container">
				<div className="row">
					<div className="col-lg-12 col-md-12"></div>
					<MenuView 	connected={this.state.connected}
								authLink={this.state.authLink}
					/>
				</div>
				<div className="row">	
					<div className="col-lg-12 col-md-12"></div>
					<ContentView 	connected={this.state.connected}
									contactData={this.state.contactData} />
				</div>
			</div>
		)
	}
	handleAuthLinkRecieved(res){
		this.setState({authLink: res.link});
	}

	handleContactData(res){
		console.log('Got contact data');
		this.setState({
			contactData: res.data
		});
	}

	requestContactData(){
		console.log("Request contact data");
		$.ajax('api/contacts').done((res) => {this.handleContactData(res)});
	}

	handleConnectionStatusRecieved(res){
		this.setState({connected: res.connected});
		this.requestContactData();
	}
	componentWillMount(){
		$.ajax('api/status').done((res) => {this.handleConnectionStatusRecieved(res)});
		$.ajax('api/getAuthLink').done((res) => {this.handleAuthLinkRecieved(res)});
	}
}