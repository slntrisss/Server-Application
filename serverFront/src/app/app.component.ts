import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { DataState } from './enum/data-state.enum';
import { Status } from './enum/status.enum';
import { AppState } from './interface/app-state';
import { CustomResponse } from './interface/custom-response';
import { Server } from './interface/server';
import { NotificationService } from './server/notification.service';
import { ServerService } from './server/server.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  appState$: Observable<AppState<CustomResponse>>;
  readonly DataState = DataState;
  readonly Status = Status;
  private filterSubject = new BehaviorSubject<string>('');
  private dataObject = new BehaviorSubject<CustomResponse>(null);
  filterStatus$ = this.filterSubject.asObservable();
  private isLoading = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoading.asObservable();



  constructor(private serverService: ServerService, private notifier: NotificationService){}

  ngOnInit(): void {
    this.appState$ = this.serverService.servers$
    .pipe(
      map(response => {
        this.dataObject.next(response);
        this.notifier.onDefault(response.message);
        return {dataState: DataState.LOADED_STATE, data: response}
      }),
      startWith({dataState: DataState.LOADING_STATE}),
      catchError((error: string) =>{
        this.notifier.onError(error);
        return of({dataState: DataState.ERROR_STATE, error: error});
      })
    )
  }

  pingServer(ipAddress: string): void {
    this.filterSubject.next(ipAddress)
    this.appState$ = this.serverService.ping$(ipAddress)
    .pipe(
      map(response => {
        const index = this.dataObject.value.data.servers
        .findIndex(server => server.id === response.data.server.id);
        this.dataObject.value.data.servers[index] = response.data.server;
        this.filterSubject.next('');
        this.notifier.onDefault(response.message);
        return {dataState: DataState.LOADED_STATE, data: this.dataObject.value}
      }),
      startWith({dataState: DataState.LOADED_STATE, data: this.dataObject.value}),
      catchError((error: string) =>{
        this.filterSubject.next('');
        this.notifier.onError(error);
        return of({dataState: DataState.ERROR_STATE, error: error});
      })
    )
  }

  filterServers (status: Status): void {
    this.appState$ = this.serverService.filter$(status, this.dataObject.value)
    .pipe(
      map(response => {
        this.notifier.onDefault(response.message);
        return {dataState: DataState.LOADED_STATE, data: response}
      }),
      startWith({dataState: DataState.LOADED_STATE, data: this.dataObject.value}),
      catchError((error: string) =>{
        this.notifier.onError(error);
        return of({dataState: DataState.ERROR_STATE, error: error});
      })
    )
  }

  saveServer(serverForm: NgForm): void {
    this.isLoading.next(true);
    this.appState$ = this.serverService.save$(<Server>serverForm.value)
    .pipe(
      map(response => {
        this.dataObject.next(
          {...response, data: { servers: [response.data.server, ...this.dataObject.value.data.servers] } }
        );
        document.getElementById('closeModal').click();
        this.isLoading.next(false);
        this.notifier.onDefault(response.message);
        serverForm.resetForm({status: this.Status.SERVER_DOWN});
        return { dataState: DataState.LOADED_STATE, data: this.dataObject.value}
      }),
      startWith({dataState: DataState.LOADED_STATE, data: this.dataObject.value}),
      catchError((error: string) =>{
        this.isLoading.next(false);
        this.notifier.onError(error);
        return of({dataState: DataState.ERROR_STATE, error: error});
      })
    )
  }

  deleteServer(deletedServer: Server): void {
    this.appState$ = this.serverService.delete$(deletedServer.id)
    .pipe(
      map(response => {
        this.dataObject.next(
          {...response, data: { servers: this.dataObject.value.data.servers.filter(server => server.id !== deletedServer.id)} }
        );
        this.notifier.onDefault(response.message);
        return { dataState: DataState.LOADED_STATE, data: this.dataObject.value}
      }),
      startWith({dataState: DataState.LOADED_STATE, data: this.dataObject.value}),
      catchError((error: string) =>{
        this.notifier.onError(error);
        return of({dataState: DataState.ERROR_STATE, error: error});
      })
    )
  }

  printReport(): void {
    this.notifier.onDefault('Report downloaded');
    let dataType = 'application/vnd.ms-excel.sheet.macroEnabled.12';
    let tableSelect = document.getElementById('servers');
    let tableHtml = tableSelect.outerHTML.replace(/ /g, '%20');
    let downloadLink = document.createElement('a');
    document.body.appendChild(downloadLink);
    downloadLink.href = 'data:' + dataType + ', ' + tableHtml;
    downloadLink.download = 'server-report.xls';
    downloadLink.click();
    document.body.removeChild(downloadLink);
  }
}
