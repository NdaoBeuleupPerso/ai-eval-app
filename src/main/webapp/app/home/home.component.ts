// import { Component, OnInit, inject, signal } from '@angular/core';
// import { RouterModule } from '@angular/router';
//
// import SharedModule from 'app/shared/shared.module';
// import { LoginService } from 'app/login/login.service';
// import { AccountService } from 'app/core/auth/account.service';
// import { Account } from 'app/core/auth/account.model';
//
// @Component({
//   selector: 'jhi-home',
//   templateUrl: './home.component.html',
//   styleUrl: './home.component.scss',
//   imports: [SharedModule, RouterModule],
// })
// export default class HomeComponent implements OnInit {
//   account = signal<Account | null>(null);
//
//   private readonly accountService = inject(AccountService);
//   private readonly loginService = inject(LoginService);
//
//   ngOnInit(): void {
//     this.accountService.identity().subscribe(account => this.account.set(account));
//   }
//
//   login(): void {
//     this.loginService.login();
//   }
// }

import { Component, OnInit, inject, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Import du Router

import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import SharedModule from 'app/shared/shared.module';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true, // Assure-toi qu'il est bien en standalone si JHipster 8+
  imports: [SharedModule, RouterModule],
})
export default class HomeComponent implements OnInit {
  account = signal<Account | null>(null);

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly router = inject(Router); // Injection du Router

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      this.account.set(account);
      if (account) {
        this.redirectUserBasedOnRole();
      }
    });
  }

  private redirectUserBasedOnRole(): void {
    // Logique de redirection automatique selon les descriptions Keycloak
    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.router.navigate(['/reference-legale']);
    } else if (this.accountService.hasAnyAuthority('ROLE_AUDITEUR')) {
      this.router.navigate(['/trace-audit']);
    } else if (this.accountService.hasAnyAuthority('ROLE_EVALUATEUR')) {
      this.router.navigate(['/appel-offre']);
    } else if (this.accountService.hasAnyAuthority('ROLE_SOUMISSIONNAIRE')) {
      this.router.navigate(['/soumissionnaire/dashboard']);
    }
  }

  // Méthode utilitaire pour le template (gestion de l'affichage des cartes)
  hasRole(authority: string): boolean {
    return this.accountService.hasAnyAuthority(authority);
  }

  login(): void {
    this.loginService.login();
  }
}
