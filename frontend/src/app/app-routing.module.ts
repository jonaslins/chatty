import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ChatRoomComponent } from './chat-room-component/chat-room.component';


const routes: Routes = [
  { path: '', component: ChatRoomComponent },
  // { path: '', redirectTo: 'shortenUrl', pathMatch: 'full', component: CreateShortUrlComponent  },
  // { path: 'shortenUrl', component: CreateShortUrlComponent },
  // { path: 'shortUrls', component: ShortUrlListComponent },
  // { path: 'details/:code', component: ShortUrlDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
