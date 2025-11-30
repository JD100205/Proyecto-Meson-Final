import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ApiService } from '../../service/api.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-editproduct',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './editproduct.component.html',
  styleUrl: './editproduct.component.css'
})
export class EditproductComponent implements OnInit {

  editProductForm!: FormGroup
  categories: any[] = []
  message: any = null
  imageUrl: any = null
  productId: string = ''
  

  constructor(private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder) { }

  ngOnInit(): void {

    this.productId = this.route.snapshot.paramMap.get('productId') || ''

    this.editProductForm = this.fb.group(
      {
        image: [null],
        categoryId: [''],
        name: [''],
        description: [''],
        price: ['']
      })

    this.apiService.getAllCategory().subscribe(res => {
      this.categories = res.categoryList;
    })

    if (this.productId) {
      this.apiService.getProductById(this.productId).subscribe(res => {
        this.editProductForm.patchValue({
          categoryId: res.product.categoryId,
          name: res.product.name,
          description: res.product.description,
          price: res.product.price
        });
        this.imageUrl = res.product.imageUrl;
      })
    }
  }

  handleImageChange(event: Event): void {
    const input = event.target as HTMLInputElement;

    if(input.files && input.files[0]){
      const file = input.files[0];
      this.editProductForm.patchValue({image: file});

      const reader = new FileReader();

      reader.onload = () =>{
        this.imageUrl = reader.result as string
      }
      reader.readAsDataURL(file)
    }
  }

  handleSubmit():void{
    const formData = new FormData();
    const formValues = this.editProductForm.value;

    if (formValues.image) {
      formData.append('image', formValues.image)
    }
    formData.append('productId', this.productId)
    formData.append('categoryId', formValues.categoryId)
    formData.append('name', formValues.name)
    formData.append('description', formValues.description)
    formData.append('price', formValues.price)

    console.log("Id del producto: " + this.productId)
    console.log("Id de la categoria: " + formValues.categoryId)
    console.log("Nombre : " + formValues.name)
    console.log("Descripcion : " + formValues.description)
    console.log("Precio: " + formValues.price)
    

    this.apiService.updateProduct(formData).subscribe({
      next:(res) =>{
        this.message = res.message
        setTimeout(()=>{
          this.message = ''
          this.router.navigate(['/admin/products'])
        }, 3000)
      },
      error:(error) =>{
        console.log(error)
        this.message = error?.error?.message || "No se pudo actualizar el producto";
      }
    })
  }


}
