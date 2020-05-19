import{Component, Input, OnInit}from '@angular/core';
import {ClrLoadingState}from '@clr/angular';
import {InboxService} from '../inbox.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

@Component({
selector: 'inbox',
templateUrl: './inbox.component.html',
styleUrls: ['./inbox.component.scss']
})
export class InboxComponent implements OnInit {
private serverUrl: string = '/ws';
private stompClient;
@Input() inboxName: string;
@Input() inboxLocation: string;
error: any;
key: string;
keySaved: boolean;
notifications: any;
saveBtnState: ClrLoadingState = ClrLoadingState.DEFAULT;


constructor(private inboxService: InboxService) {
    console.log("inbox component created...");
  }

  initializeWebSocketConnection(){
    console.log("Connecting to ws socket...");
    let ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(ws);

    this.stompClient.connect({}, function(frame) {
      this.stompClient.subscribe("/notifications", (message) => {
          console.log(message);
        if(message.body) {
           this.onMessageReceived(message);
        }
      });
    });
  }

 onMessageReceived(message) {
        console.log("Message Recieved from Server :: " + message);
        console.log(JSON.stringify(message.body));
    }

  _disconnect() {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error) {
        console.log("errorCallBack -> " + error)
        setTimeout(() => {
            console.log("retrying... -> ")
        }, 5000);
    }

  ngOnInit(): void {
    console.log("inbox called with " + this.inboxName + " & location: " + this.inboxLocation);
    this.getInbox();
    this.getInboxNotifications();
    this.initializeWebSocketConnection();
  }

  saveKey() {
    this.error = null;
    this.saveBtnState = ClrLoadingState.LOADING;
    console.log("getting inbox notifications.." + this.inboxName)
    this.inboxService.saveConfigKey(this.inboxName, this.key)
      .subscribe((data: any) => {
        this.notifications = data;
        this.keySaved = true;
        this.saveBtnState = ClrLoadingState.DEFAULT;
      },
        error => {
          this.error = error;
          this.saveBtnState = ClrLoadingState.DEFAULT;
        });
  }

  getInbox() {
    this.error = null;
    this.inboxService.getInbox(this.inboxName)
      .subscribe((data: any) => {
        this.inboxLocation = data.location;
        this.keySaved = data.configKeySaved;
      },
        error => {
          this.error = error;
        });
  }

  getInboxNotifications() {
    this.error = null;
    console.log("getting inbox notifications.." + this.inboxName)
    this.inboxService.getInboxNotifications(this.inboxName)
      .subscribe((data: any) => {
        this.notifications = data;
      },
        error => {
          this.error = error;
        });
  }
}
