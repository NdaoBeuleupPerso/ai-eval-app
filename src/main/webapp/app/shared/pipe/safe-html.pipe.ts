import { Pipe, PipeTransform, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'safeHtml',
  standalone: true,
})
export class SafeHtmlPipe implements PipeTransform {
  private sanitizer = inject(DomSanitizer);

  transform(value: string | null | undefined): SafeHtml {
    if (!value) return '';

    let cleaned = value;

    // 1. On enlève les blocs de code Markdown (les ```html)
    cleaned = cleaned.replace(/```html/gi, '');
    cleaned = cleaned.replace(/```/gi, '');

    // 2. On enlève les balises qui bloquent le navigateur mais on GARDE le contenu
    // On ne fait plus de "substring", on fait du "replace" pour ne pas perdre de texte
    cleaned = cleaned.replace(/<!DOCTYPE html>/gi, '');
    cleaned = cleaned.replace(/<html[^>]*>/gi, '');
    cleaned = cleaned.replace(/<\/html>/gi, '');
    cleaned = cleaned.replace(/<head>[\s\S]*?<\/head>/gi, ''); // Supprime le head ET son contenu (styles IA)
    cleaned = cleaned.replace(/<body[^>]*>/gi, '');
    cleaned = cleaned.replace(/<\/body>/gi, '');

    // 3. Sécurité : Si après nettoyage il reste du texte, on l'affiche
    return this.sanitizer.bypassSecurityTrustHtml(cleaned.trim());
  }
}
