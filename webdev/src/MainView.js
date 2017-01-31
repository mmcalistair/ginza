import React from 'react';
import ReactDOM from 'react-dom';

class ContentView extends React.Component{
	constructor(props){
 		super(props);           
	}

	render(){
		return(
			<p>Content</p>
		)
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
 			authLink : null
 		};           

 		this.handleAuthLinkRecieved = this.handleAuthLinkRecieved.bind(this);
 		this.handleConnectionStatusRecieved = this.handleConnectionStatusRecieved.bind(this);
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
					<ContentView connected={this.state.connected}/>
				</div>
			</div>
		)
	}
	handleAuthLinkRecieved(res){
		this.setState({authLink: res.link});
	}

	handleConnectionStatusRecieved(res){
		this.setState({connected: res.connected})
	}
	componentWillMount(){
		$.ajax('api/status').done((res) => {this.handleConnectionStatusRecieved(res)});
		$.ajax('api/getAuthLink').done((res) => {this.handleAuthLinkRecieved(res)});
	}
}