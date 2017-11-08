/* PROGRAM IMPLEMENTATION OF SINGLE LINKED LIST */
#include"stdio.h"
#include <string.h>
#include <stdlib.h>

//#define NULL 0
/* STRUCTURE CONTANING A DATA PART AND A LINK PART */
struct node
{
	char *str;
    long int data;
    struct node *next;
}*p;

/* P IS A GLOBAL POINTER CONTAINS THE ADRESS OF THE FIRST NODE IN 
 LIST
 */

/*It gets the input and make a string*/
char *fgets_enhanced(FILE *f)
{
    
    int chunksize = 4; /* use BUFSIZ-1 for (probably) more efficient program */
    char *s;
    char *temp;
    int buffersize = chunksize+1;
    char* nPos;
    
    s = malloc(buffersize); /* get the first chunk */
    if (s == NULL) {
        printf("Can't allocate %d bytes\n", buffersize);
        return NULL;
    }
    /* read the first chunk with standard library fgets */
    if (fgets(s, chunksize+1, f) == NULL) {
        printf("fgets returned NULL\n");
    }
    else {
        nPos = strchr(s, '\n');
        while (nPos == NULL) {
            buffersize += chunksize;
            temp = realloc(s, buffersize);
            if (temp == NULL) {
                printf("Can't realloc %d bytes\n", chunksize);
                free(s); /* clean up the previously allocated stuff */
                s = NULL;
                break;
            }
            else {
                s = temp;
                /*read into the zero byte that was the end of the previous chunk */
                if (fgets(s+buffersize-chunksize-1, chunksize+1, f) == NULL) {
                    printf("fgets returned NULL\n");
                    break;
                }
            }
            nPos = strchr(s, '\n');
        }
    }
    /*The last character is null*/
    s[(int)(nPos-s)] = 0;
    return s;
}


/*THIS FUNCTION DELETES A NODE */
void delNode(long int num)
{
    struct node *temp, *m;
    temp=p;
    while(temp!=NULL)
    {
        if(temp->data==num)
        {
            if(temp==p)
            {
                p=temp->next;
            }else
            {
                m->next=temp->next;
            }
            /*Fix for Q2(a)*/
	    free(temp->str);
            free(temp);
            return;
            
            
        }else
        {
            m=temp;
            temp= temp->next;
        }
        
    }
    printf("ELEMENT %ld NOT FOUND ", num);
}

/*THIS FUNCTION DELETES ALL NODES OF LINKED LIST */
void delAll()
{
    struct node *temp,*temp2;
    temp=p;
    while(temp!=NULL)
    {
        temp2=temp;
        temp= temp->next;
        
        free(temp2->str);
        free(temp2);
        
    }
    /*Fix for Q2(b)*/
    p=NULL;
    
}

/* ADD A NEW NODE AT BEGINNING */
void addBeg(long int num, char* name )
{
    
    
    /* CREATING A NODE AND INSERTING VALUE TO IT */
    struct node *temp;
    temp=(struct node *)malloc(sizeof(struct node));
    temp->data=num;
    temp->str=name;
    
    /* IF LIST IS NULL ADD AT BEGINNING */
    if ( p== NULL)
    {
        p=temp;
        p->next=NULL;
    }
    
    else
    {
        temp->next=p;
        p=temp;
    }
}

/*IT ADDS A NEW NODE AFTER THE LOCATION*/
void addAfter(long int num, int loc, char* name)
{
    int i;
    struct node *temp,*t,*r;
    r=p; /* here r stores the first location */
    if(loc > count()+1 || loc <= 0)
    {
        printf("insertion is not possible!\n");
        return;
    }
    if (loc == 1)/* if list is null then add at beginning */
    {
        addBeg(num,name);
        return;
    }
    else
    {
        for(i=1;i<loc;i++)
        {
            t=r; /* t will be holding previous value */
            r=r->next;
        }
        temp=(struct node *)malloc(sizeof(struct node));
        temp->data=num;
        temp->str=name;
        t->next=temp;
        t=temp;
        t->next=r;
        return;
    }
}

/*THIS FUNCTION CHANGES THA VALUE OF A NODE*/
void edit(long int oldNum, long int newNum, char* name )
{
    struct node *temp, *m;
    temp=p;
    while(temp!=NULL)
    {
        if(temp->data==oldNum)
        {
            temp->data = newNum;
            free(temp->str);
            temp->str = name;
            return;
        }else
        {
            m=temp;
            temp= temp->next;
        }
        
    }
    printf("ELEMENT %ld NOT FOUND \n", oldNum);
}

/*THIS FUNCTION DUPLICATES A NODE AND ADDS THE NEW NODE AFTER THE OLD NODE*/
void duplicate(long int num)
{
    //error
    struct node *temp=p;
    int i = 1;
    while(temp!=NULL)
    {
        if(temp->data==num)
        {
            long len = strlen(temp->str)>0 ? strlen(temp->str) : 0;
            /*Fix for Q2(c)*/
	    // char* name = malloc(len);
	    char* name = malloc(len+1);
            strcpy(name, temp->str);
            addAfter(temp->data, i, name);
            return;
        }else
        {
            i++;
            temp= temp->next;
        }
        
    }
    printf("ELEMENT %ld NOT FOUND \n", num);
}


/*THIS FUNCTION ADDS A NODE AT THE LAST OF LINKED LIST */
void append( long int num, char* name )
{
    struct node *temp,*r;
    /* CREATING A NODE AND ASSIGNING A VALUE TO IT */
    
    temp= (struct node *)malloc(sizeof(struct node));
    temp->data= num;
    temp->str= name;
    r=(struct node *)p;
    
    if (p == NULL) /* IF LIST IS EMPTY CREATE FIRST NODE */
    {
        p=temp;
        p->next =NULL;
    }
    else
    { /* GO TO LAST AND ADD*/
        
        while( r->next != NULL)
            r=r->next;
        r->next =temp;
        r=temp;
        r->next=NULL;
    }
}

/* THIS FUNCTION DISPLAYS THE CONTENTS OF THE LINKED LIST */
void display()
{
    struct node *r=p;
    if(r==NULL)
    {
        printf("NO ELEMENT IN THE LIST\n");
        return;
    }
    /* traverse the entire linked list */
    while(r!=NULL)
    {
        printf(" -> %li",r->data);
        printf(", %s",r->str);
        
        r=r->next;
    }
    printf("\n");
}

//THIS FUNCTION COUNTS THE NUMBER OF ELEMENTS IN THE LIST
int count()
{
    struct node *n;
    int c=0;
    n=p;
    while(n!=NULL)
    {
        n=n->next;
        c++;
    }
    return(c);
}


/* THIS IS THE MAIN PROGRAM */
int main()
{
    char c;
    p=NULL;
    while(1) /* this is an indefinite loop */
    {
	printf("[(i)nsert,(d)elet,delete (a)ll,d(u)plicate,(e)dit,(p)rint,e(x)it]:");
        
        scanf("%s",&c);; /* ENTER A VALUE FOR SWITCH */
        
        c = (c>64 && c< 91) ? c + 32 : c;
        
        switch(c)
        {
            case 'i':
            {
                long int num;
                printf("enter the tel:>");
                scanf("%li",&num);
                printf("enter the name:>");
                getchar();
                append(num, fgets_enhanced(stdin));
                break;
            }
            case 'd':
            {
                long int num;
                printf("enter the tel :>");
                scanf("%li",&num);
                delNode(num);
                break;
            }
            case 'e':
            {
                long int oNum, nNum;
                char* name;
                printf("enter the old tel :>");
                scanf("%li",&oNum);
                printf("enter the new tel :>");
                scanf("%li",&nNum);
                printf("enter the new name:>");
                getchar();
                name = fgets_enhanced(stdin);
                edit(oNum, nNum, name);
                break;
            } 
            case 'p':
            {
                printf("The elements are :> ");
                display();
                break;
            }
            case 'a':
            {
                delAll();
                break;
            }
            case 'u':
            {
                long int num;
                printf("enter the tel :>");
                scanf("%li",&num);
                duplicate(num);
                break;
            }
            case 'x':
            {
                //error
                delAll();
                printf("bye\n");
                //exit(0);
                return 0;
            } 
        }/* end if switch */
    }/* end of while */
}/* end of main */
